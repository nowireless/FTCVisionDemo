package com.kennedyrobotics.visiondemo;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {

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
    private Scalar lowThres = new Scalar(67, 60, 66);
    private Scalar highThres = new Scalar(108, 255, 255);
    public int minContours = 4;
    public double approxEpsilon = 5;
    private final Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)); // TODO For dialate it was using 5x5 for some reason

    private final Mat hierarchy = new Mat();

    public final Mat thresholdImage = new Mat();
    public final Mat morphImage = new Mat();
    public final Mat annotatedImage = new Mat();

    private final List<MatOfPoint> imageContoursTemp = new ArrayList<>();
    private final List<MatOfPoint> imageContours = new ArrayList<MatOfPoint>();
    private final List<MatOfPoint> imagePolyContours = new ArrayList<MatOfPoint>();


    public Mat processFrame(Mat sourceImage) {
        imageContoursTemp.clear();
        imageContours.clear();
        imagePolyContours.clear();

        //
        // Extract features from image
        //

        // Blur image
        Imgproc.GaussianBlur(sourceImage, thresholdImage, new Size(5, 5), 0);

        // Convert to HSV Color space
        Imgproc.cvtColor(thresholdImage, thresholdImage, Imgproc.COLOR_BGR2HSV);

        // Threshold image
        Core.inRange(thresholdImage, lowThres, highThres, thresholdImage);

        // Morphology/Cleanup
        Imgproc.morphologyEx(thresholdImage, morphImage, Imgproc.MORPH_RECT, morphKernel);
        Imgproc.erode(morphImage, morphImage, morphKernel);
        Imgproc.dilate(morphImage, morphImage, morphKernel);

        // Find contours in image
        Imgproc.findContours(morphImage, imageContoursTemp, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("Contour size" + imageContoursTemp.size());

        for (MatOfPoint imgContour : imageContoursTemp) {
            MatOfPoint2f contour2f = new MatOfPoint2f(imgContour.toArray());
            MatOfPoint2f approxCruve = new MatOfPoint2f();

            Imgproc.approxPolyDP(contour2f, approxCruve, approxEpsilon, true);
            int size = approxCruve.toArray().length;
            if (size >= minContours) {
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
