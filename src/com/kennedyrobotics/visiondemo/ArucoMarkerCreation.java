package com.kennedyrobotics.visiondemo;

import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ArucoMarkerCreation {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat markerImage = new Mat();
        Dictionary dict =  Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
        Aruco.drawMarker(dict, 23, 200, markerImage, 1);

        Imgcodecs.imwrite("marker23.png", markerImage);
    }
}
