package com.kennedyrobotics.visiondemo;

import org.nowireless.vision.gui.JVideoPanel;

import javax.swing.*;

public class VisionGUI extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel container;
    public JVideoPanel input;
    public JVideoPanel output;
    public JVideoPanel morph;
    public JVideoPanel threshold;

    public VisionGUI() {
        setContentPane(container);
    }



}
