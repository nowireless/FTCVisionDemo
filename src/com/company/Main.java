package com.company;

import org.nowireless.vision.util.MatUtil;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

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


        // Setup the Gui!
        VisionGUI frame = new VisionGUI();

        // Put something in each of the images
        frame.input.updateImage(MatUtil.makeTextImage("Waiting..."));
        frame.output.updateImage(MatUtil.makeTextImage("Waiting..."));

        frame.setTitle("Vision Demo");
        frame.setSize(750, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
