package org.nowireless.vision.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.nowireless.common.Initializable;
import org.nowireless.common.gui.BufferedImageCache;
import org.nowireless.vision.util.MatUtil;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

/**
 * A extension of a {@link JPanel} to make it easy to display a {@link Mat} image in Swing.
 * @author nowireless
 *
 */
public class JVideoPanel extends JPanel implements Initializable{

	private static final long serialVersionUID = 3570076862706867432L;
	
	private transient final Size size;
	private transient final Object imageLock = new Object();
	private transient final BufferedImageCache cache = new BufferedImageCache();
	private transient BufferedImage jImage = null;
	
	public JVideoPanel() {
		this(640, 480);
	}
	
	public JVideoPanel(int width, int height) {
		size = new Size(width, height);
		this.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void init() {
		Mat black = new Mat(this.size, CvType.CV_8SC3, new Scalar(0,0,0));
		this.updateImage(black);
		black.release();
	}

	@Override
	public void deinit() {
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		synchronized (imageLock) {
			g.drawImage(jImage, 0, 0, null);
		}
	}
	
	/**
	 * Updates the the internal {@link BufferedImage} to the given {@link Mat}.
	 * Once set it calls {@link #repaint()}
	 * @param image
	 */
	public void updateImage(Mat image) {
		if(image == null) {
			return;
		}
		
		if(image.empty()) {
			return;
		}
		
		synchronized (imageLock) {
			if(jImage != null) {
				jImage.flush();
			}
			
			jImage = MatUtil.matToBufferedImage(image, cache);
		}
		
		repaint();
	}

}
