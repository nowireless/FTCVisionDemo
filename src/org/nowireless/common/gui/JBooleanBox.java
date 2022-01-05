package org.nowireless.common.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class JBooleanBox extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final Color offColor = Color.RED;
	private final Color onColor = Color.GREEN;
	
	private volatile boolean value = false;
	
	public JBooleanBox() {
		this(10,10);
	}
	
	public JBooleanBox(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
	}
	
	public void set(boolean state) {
		this.value = state;
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(value) {
			this.setBackground(onColor);
		} else {
			this.setBackground(offColor);
		}
	}
}
