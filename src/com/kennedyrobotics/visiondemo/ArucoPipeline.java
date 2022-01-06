package com.kennedyrobotics.visiondemo;

import org.nowireless.vision.util.MatUtil;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.highgui.ImageWindow;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.openftc.easyopencv.OpenCvPipeline;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ArucoPipeline extends OpenCvPipeline {


    @Override
    public Mat processFrame(Mat input) {
        List<Mat> markerCorners = new ArrayList<>();
        List<Mat> rejectedCandidates = new ArrayList<>();
        Mat markerIDs = new Mat();

        Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
        DetectorParameters parameters = DetectorParameters.create();
        Aruco.detectMarkers(input, dict, markerCorners, markerIDs, parameters, rejectedCandidates);

        Mat output = input.clone();
        Aruco.drawDetectedMarkers(output, markerCorners, markerIDs);

        return output;
    }

    public static void main(String[] args) {
        // Load in the OpenCV Libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        HighGui.namedWindow("output");

        VideoCapture camera = new VideoCapture();

        int cameraIndex = 0;
        camera.open(cameraIndex);

        // Run the camera at 640x480
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);

        var pl = new ArucoPipeline();


        Mat sourceImage = new Mat();
        HighGui.imshow("output", MatUtil.makeTextImage("Waiting..."));
        HighGui.waitKey(1);
        HighGui.windows.get("output").frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        while(true) {
            boolean isOpened = camera.isOpened();
            if (isOpened) {
                if(camera.grab()) {
                    System.out.println("Retrieving Camera Frame");
                    camera.retrieve(sourceImage);
                    if (!sourceImage.empty()) {
                        System.out.println("TRACE: Got camera frame");

                        Mat outputImage = pl.processFrame(sourceImage);
                        System.out.println("TRACE: Processed framed");

                        HighGui.imshow("output", outputImage);
                    } else {
                        System.out.println("WARN: Image from camera is empty");
                    }
                } else {
                    System.out.println("TRACE: Could not grab camera frame");
                    System.out.println("TRACE: Releasing Camera");
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
