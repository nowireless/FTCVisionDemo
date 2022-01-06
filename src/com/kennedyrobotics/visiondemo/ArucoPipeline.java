package com.kennedyrobotics.visiondemo;

import org.nowireless.vision.util.MatUtil;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.openftc.easyopencv.OpenCvPipeline;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ArucoPipeline extends OpenCvPipeline {

    public enum MarkerPlacement {
        LEFT,
        CENTER,
        RIGHT,
        UNKNOWN
    }

    @Override
    public Mat processFrame(Mat input) {
        List<Mat> markerCorners = new ArrayList<>();
        MatOfInt markerIDs = new MatOfInt();

        //
        // Detect Aruco markers
        //
        Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
        DetectorParameters parameters = DetectorParameters.create();
        Aruco.detectMarkers(input, dict, markerCorners, markerIDs, parameters);

        //
        // Determine which team marker spot it is on (If markers are present)
        //
        Rect leftArea = new Rect(
                new Point(0,0),
                new Point(input.width()*(1.0/3.0), input.height())
        );
        Rect centerArea = new Rect(
                new Point(input.width()*(1.0/3.0),0),
                new Point(input.width()*(2.0/3.0), input.height())
        );
        Rect rightArea = new Rect(
                new Point(input.width()*(2.0/3.0),0),
                new Point(input.width(), input.height())
        );

        MarkerPlacement markerPlacement = MarkerPlacement.UNKNOWN;

        int[] markerIDArray = new int[0];
        if (!markerCorners.isEmpty()) {
            markerIDArray = markerIDs.toArray();
        }
        for (int i = 0; i < markerCorners.size(); i++) {
            int markerID = markerIDArray[i];
            var corners = markerCorners.get(i);
            var moments = Imgproc.moments(corners);

            // https://docs.opencv.org/3.4/dd/d49/tutorial_py_contour_features.html
            double cx = moments.m10/moments.m00;
            double cy = moments.m01/moments.m00;
            Point centerOfMass = new Point(cx, cy);

            // We only care about marker with id 23
            if (markerID != 23) {
                System.out.println("Ignoring marker: id="+markerID + " cx="+cx + " cy="+cy);
                continue;
            }

            // Figure out which region of the image the marker is in.
            if(leftArea.contains(centerOfMass)) {
                markerPlacement = MarkerPlacement.LEFT;
            } else if(centerArea.contains(centerOfMass)) {
                markerPlacement = MarkerPlacement.CENTER;
            } else if(rightArea.contains(centerOfMass)) {
                markerPlacement = MarkerPlacement.RIGHT;
            }

            System.out.println("Found marker: id="+markerID + " placement=" + markerPlacement+ " cx="+cx + " cy="+cy);
            break;
        }


        //
        // Annotate the source image
        //
        Mat output = input.clone();

        double leftAlpha = 0.1;
        double centerAlpha = 0.1;
        double rightAlpha = 0.1;

        if (markerPlacement == MarkerPlacement.LEFT) {
            leftAlpha = 0.75;
        } else if (markerPlacement == MarkerPlacement.CENTER) {
            centerAlpha = 0.75;
        } else if (markerPlacement == MarkerPlacement.RIGHT) {
            rightAlpha = 0.75;
        }

        drawTransparentRect(leftArea ,new Scalar(0,0,255), leftAlpha, output);
        drawTransparentRect(centerArea ,new Scalar(0,255,0), centerAlpha, output);
        drawTransparentRect(rightArea ,new Scalar(255,0,0),  rightAlpha, output);

        Aruco.drawDetectedMarkers(output, markerCorners, markerIDs);


        return output;
    }

    private void drawTransparentRect(Rect rect, Scalar color, double alpha, Mat dest) {
        Mat overlay = dest.clone();
        Imgproc.rectangle(overlay, rect, color, -1);

        Core.addWeighted(overlay, alpha, dest, 1, 1-alpha, dest);
    }

    public static void main(String[] args) {
        // Load in the OpenCV Libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        HighGui.namedWindow("output", HighGui.WINDOW_NORMAL);

        VideoCapture camera = new VideoCapture();

        int cameraIndex = 0;
        camera.open(cameraIndex);
        int width = (int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int height = (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT);

        System.out.println("Camera opened with "+width+"x"+height);

        // Run the camera at 640x480
        //camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        //camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);

        var pl = new ArucoPipeline();


        Mat sourceImage = new Mat();
        HighGui.imshow("output", MatUtil.makeTextImage("Waiting...", height, width));
        HighGui.waitKey(1);
        HighGui.windows.get("output").frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while(true) {
            boolean isOpened = camera.isOpened();
            if (isOpened) {
                if(camera.grab()) {
                    //System.out.println("Retrieving Camera Frame");
                    camera.retrieve(sourceImage);
                    if (!sourceImage.empty()) {
                        //System.out.println("TRACE: Got camera frame");

                        Mat outputImage = pl.processFrame(sourceImage);
                        //System.out.println("TRACE: Processed framed");

                        HighGui.imshow("output", outputImage);
                    } else {
                        System.out.println("WARN: Image from camera is empty");
                    }
                } else {
                    System.out.println("WARN: Could not grab camera frame");
                    System.out.println("WARN: Releasing Camera");
                    camera.release();
                    camera = new VideoCapture();
                }
            } else {
                System.out.println("WARN: Camera is not opened, opening...");
                camera.open(cameraIndex);
            }
            HighGui.waitKey(1);
        }
    }
}
