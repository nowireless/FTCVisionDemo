package com.kennedyrobotics.visiondemo;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

public class HSVPipeline extends OpenCvPipeline {

    public static class Config {
        public Scalar lowThreshold = new Scalar(104, 81, 78);
        public Scalar highThreshold = new Scalar(126, 139, 154);
        public int minContours = 4;
        public double approxEpsilon = 5;
    }

    /*
     * Colors
     */
    static final Scalar TEAL = new Scalar(3, 148, 252);
    static final Scalar PURPLE = new Scalar(158, 52, 235);
    static final Scalar RED = new Scalar(255, 0, 0);
    static final Scalar GREEN = new Scalar(0, 255, 0);
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final int CONTOUR_LINE_THICKNESS = 2;

    // Configuration
    public final Config config = new Config();


    private final Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
    private final Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(6, 6));
    private final Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

    private final Mat closeSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
    private final Mat openSE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));

    /*
     * Buffers
     */
    private final Mat hierarchy = new Mat();
    public final Mat thresholdImage = new Mat();
    public final Mat morphImage = new Mat();
    public final Mat annotatedImage = new Mat();

    private final List<MatOfPoint> imageContoursTemp = new ArrayList<>();
    private final List<MatOfPoint> imageContours = new ArrayList<MatOfPoint>();
    private final List<MatOfPoint> imagePolyContours = new ArrayList<MatOfPoint>();


    @Override
    public Mat processFrame(Mat sourceImage) {
        imageContoursTemp.clear();
        imageContours.clear();
        imagePolyContours.clear();

        //
        // Extract features from image
        //

        // Blur image
        // Imgproc.GaussianBlur(sourceImage, thresholdImage, new Size(5, 5), 0);

        // Convert to HSV Color space
        Imgproc.cvtColor(sourceImage, thresholdImage, Imgproc.COLOR_BGR2HSV);

        // Threshold, find pixels withing rnage
        Core.inRange(thresholdImage, config.lowThreshold, config.highThreshold, thresholdImage);

        /*
         * Morphology
         * First a closing operation is preformed to help group blobs that are close together
         * into a single blob
         * Then a opening operation is preformed to help remove isolated noisy areas.
         * https://stackoverflow.com/questions/30369031/
         * remove-spurious-small-islands-of-noise-in-an-image-python-opencv
         */
         //Imgproc.morphologyEx(thresholdImage, morphImage, Imgproc.MORPH_RECT, morphKernel);
         Imgproc.erode(thresholdImage, morphImage, erodeKernel);
         Imgproc.dilate(morphImage, morphImage, dilateKernel);

        Imgproc.morphologyEx(morphImage, morphImage, Imgproc.MORPH_CLOSE, closeSE);
        Imgproc.morphologyEx(morphImage, morphImage, Imgproc.MORPH_OPEN, openSE);


        // Find contours in image
        Imgproc.findContours(morphImage, imageContoursTemp, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("Contour size" + imageContoursTemp.size());

        for (MatOfPoint imgContour : imageContoursTemp) {
            MatOfPoint2f contour2f = new MatOfPoint2f(imgContour.toArray());
            MatOfPoint2f approxCruve = new MatOfPoint2f();

            Imgproc.approxPolyDP(contour2f, approxCruve, config.approxEpsilon, true);
            int size = approxCruve.toArray().length;
            if (size >= config.minContours) {
                MatOfPoint contourPoly = new MatOfPoint(approxCruve.toArray());
                imagePolyContours.add(contourPoly);
                imageContours.add(imgContour);
            }
            contour2f.release();
        }

        //
        // Identify contours
        //

        //
        // Annotated image
        //
        sourceImage.copyTo(annotatedImage);
        Imgproc.drawContours(annotatedImage, imagePolyContours, -1, BLUE, CONTOUR_LINE_THICKNESS, 8);
        return annotatedImage;
    }

}
