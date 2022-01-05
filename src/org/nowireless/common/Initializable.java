package org.nowireless.common;

/**
 * Interface for Initializing and deinitializing.
 * @author nowireless
 *
 */
public interface Initializable {
	
	/**
	 * Run the initializing code. 
	 */
	public void init();
	
	/**
	 * Run the deinitializing code. 
	 */
	public void deinit();
}
