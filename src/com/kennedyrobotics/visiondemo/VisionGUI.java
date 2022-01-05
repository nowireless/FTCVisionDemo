package com.kennedyrobotics.visiondemo;

import org.nowireless.vision.gui.JVideoPanel;
import org.opencv.highgui.HighGui;

import javax.swing.*;

public class VisionGUI extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel container;
    public JVideoPanel input;
    public JVideoPanel output;

    public VisionGUI() {
        setContentPane(container);
    }



}
