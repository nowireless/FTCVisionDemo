package org.nowireless.common.gui;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class BufferedImageCache {

	//<type, <buffer size, Image>>
	private final Map<Integer, Map<Integer, BufferedImage>> cache = new HashMap<Integer, Map<Integer,BufferedImage>>();
	
	public BufferedImage get(int type, int channels, int rows, int cols) {
		Map<Integer, BufferedImage> buffers = cache.get(type);
		if(buffers == null) {
			cache.put(type, new HashMap<Integer, BufferedImage>());
			buffers = cache.get(type);
		}
		
		int bufferSize = channels * rows * cols;
		BufferedImage ret = buffers.get(bufferSize);
		if(ret == null) {
			buffers.put(bufferSize, new BufferedImage(cols, rows, type));
			ret = buffers.get(bufferSize);
		}
		
		return ret;
	}
	
}
