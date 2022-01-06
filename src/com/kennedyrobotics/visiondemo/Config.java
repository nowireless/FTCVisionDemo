package com.kennedyrobotics.visiondemo;

import org.opencv.core.Scalar;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Config extends JFrame {

    // TODO Should this and the Scalar values live somewhere else?
    private class ConfigUpdater implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent arg0) {
            Scalar low = new Scalar(lowHueSlider.getValue(), lowSaturationSlider.getValue(), lowValueSlider.getValue());
            Scalar high = new Scalar(highHueSlider.getValue(), highSaturationSlider.getValue(), highValueSlider.getValue());

            setLowThres(low);
            setHighThres(high);
        }

    }

    private Scalar lowThres = new Scalar(67, 60, 66);

    public Config() {
        ConfigUpdater updater = new ConfigUpdater();
        lowHueSlider.addChangeListener(updater);
        lowSaturationSlider.addChangeListener(updater);
        lowValueSlider.addChangeListener(updater);
        highHueSlider.addChangeListener(updater);
        highSaturationSlider.addChangeListener(updater);
        highValueSlider.addChangeListener(updater);

        setContentPane(container);
    }

    public Scalar getLowThres() {return lowThres;}
    public void setLowThres(Scalar s) {
        this.lowThres = s.clone();
        //this.changed();
    }

    private Scalar highThres = new Scalar(108, 255, 255);
    public Scalar getHighThres() {return highThres;}
    public void setHighThres(Scalar s) {
        this.highThres = s.clone();
        //this.changed();
    }

    private JSlider lowHueSlider;
    private JSlider lowSaturationSlider;
    private JSlider lowValueSlider;
    private JSlider highHueSlider;
    private JSlider highSaturationSlider;
    private JSlider highValueSlider;
    private JPanel container;

}
