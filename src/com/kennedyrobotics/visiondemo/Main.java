package com.kennedyrobotics.visiondemo;

import org.nowireless.vision.util.MatUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Load in the OpenCV Libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        // Apply a theme so this looks like this program was written this century
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("Could not set system look and feel");
            e.printStackTrace();
        }

        //
        // Setup the Gui!
        //

        // Main Window frame
        VisionGUI frame = new VisionGUI();

        // Put something in each of the images
        frame.input.updateImage(MatUtil.makeTextImage("Waiting..."));
        frame.output.updateImage(MatUtil.makeTextImage("Waiting..."));

        frame.setTitle("Vision Demo");
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Config window
        Config configFrame = new Config();
        configFrame.setTitle("Vision Config");
        configFrame.setSize(400, 600);
        configFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        configFrame.setVisible(true);


        // Open camera
        VideoCapture camera = new VideoCapture();
        int cameraIndex = 0;
        camera.open(cameraIndex);

        // Lets force it to 640x480
        camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);

        Mat sourceImage = new Mat();

        // Setup pipeline
        Pipeline pipeline = new Pipeline();

        while(true) {
            boolean isOpened = camera.isOpened();
            if (isOpened) {
                if(camera.grab()) {
                    System.out.println("Retrieving Camera Frame");
                    camera.retrieve(sourceImage);
                    if (!sourceImage.empty()) {
                        System.out.println("TRACE: Got camera frame");
                        Mat outputImage = pipeline.processFrame(sourceImage);
                        System.out.println("TRACE: Processed framed");
                        // TODO something with the image
                        frame.input.updateImage(sourceImage);
                        frame.threshold.updateImage(pipeline.thresholdImage);
                        frame.morph.updateImage(pipeline.morphImage);
                        frame.output.updateImage(outputImage);


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
        }

    }
}
