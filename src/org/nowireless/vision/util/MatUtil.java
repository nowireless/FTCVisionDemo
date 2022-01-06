package org.nowireless.vision.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.nowireless.common.gui.BufferedImageCache;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MatUtil {

	public static transient final Size DEFAULT_SIZE = new Size(640, 480);
	public static transient final int DEFAULT_IMAGE_TYPE = CvType.CV_8UC3;
	public static transient final Scalar DEFAULT_COLOR = new Scalar(0,0,0);
	
	
	/**
	 * Encodes the provided {@link Mat} into {@link MatOfByte} containing
	 * the jpg encoding. 
	 * @param image Source image
	 * @param imageBuff Destination buffer
	 * @return success
	 */
	public static boolean matToJPGBuffer(Mat image,MatOfByte imageBuff) {
		return Imgcodecs.imencode(".jpg", image, imageBuff);
	}
	
	/**
	 * Decodes the provided {@link MatOfByte} and returns an image of {@link Mat}
	 * @param imageBuff Source buffer
	 * @return Image
	 */
	public static Mat jpgBufferToMat(MatOfByte imageBuff) {
		return Imgcodecs.imdecode(imageBuff, Imgcodecs.IMREAD_COLOR);
	}
	
	private static byte[] BUFFER = new byte[0];

	/**
	 * Converts the given {@link Mat} image into a {@link BufferedImage}.
	 * Code came from OpenCV Q+A user Daniel Baggio
	 * http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
	 * @param src The source Image
	 * @return
	 */
	public static BufferedImage matToBufferedImage(Mat src, BufferedImageCache cache) {
		if(src.cols() * src.rows() == 0) {
			return null;
		}
		
		BufferedImage ret;
		
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if(src.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		
		int bufferSize = src.channels() * src.cols() * src.rows();
		if(bufferSize > BUFFER.length) {
			BUFFER = new byte[bufferSize];
		}		
		src.get(0, 0, BUFFER);
		
		ret = cache.get(type, src.channels(), src.rows(), src.cols());
		
		final byte[] targetPixels = ((DataBufferByte) ret.getRaster().getDataBuffer()).getData();
		System.arraycopy(BUFFER, 0, targetPixels, 0, bufferSize);
		
		return ret;
	}
	
	/**
	 * Creates a image with the given {@link String}
	 * @param text The text to be on the image.
	 * @return The {@link Mat} containing the image.
	 */
	public static Mat makeTextImage(String text) {
		return makeTextImage(text, 480, 640);
	}

	public static Mat makeTextImage(String text, int height, int width) {
		Mat ret = Mat.ones(height, width, CvType.CV_8UC3);
		ret.setTo(new Scalar(125,125,125));
		Imgproc.putText(ret, text, new Point(20, 460), Imgproc.FONT_HERSHEY_SCRIPT_COMPLEX, 1.5, new Scalar(0, 0, 255));
		return ret;
	}
	
	/**
	 * Returns a blank image based on {@link #DEFAULT_IMAGE_TYPE}, {@link #DEFAULT_SIZE}, {@link #DEFAULT_COLOR}
	 * @return The image
	 */
	public static Mat getBlankMat() {
		return new Mat(DEFAULT_SIZE, DEFAULT_IMAGE_TYPE, DEFAULT_COLOR);
	}
	
}
