package org.nowireless.common.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * The method need to be called in the swing/GUI thread
 * @author Ryan
 *
 */

@Target(ElementType.METHOD)
public @interface Swing {

}
