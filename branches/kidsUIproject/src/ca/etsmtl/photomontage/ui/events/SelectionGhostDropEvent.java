/*
 * SmartPhotomontage
 * Copyright (C) 2007
 * François Proulx, Olivier Bilodeau, Jean-Philippe Plante, Kim Lebel
 * http://poissonimageediting.googlecode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package ca.etsmtl.photomontage.ui.events;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.developpez.gfx.swing.drag.GhostDropEvent;

/**
 * SelectionGhostDropEvent managed ghost drop events
 *
 */
public class SelectionGhostDropEvent extends GhostDropEvent {
	private final BufferedImage srcImage, maskImage, maskedSrcImage;
	
	/**
	 * Constructor
	 * @param srcImage
	 * @param maskImage
	 * @param maskedSrcImage
	 * @param point
	 */
	public SelectionGhostDropEvent(BufferedImage srcImage, BufferedImage maskImage, BufferedImage maskedSrcImage, Point point) {
		super(null, point);
		
		// Copy a reference to the portion of the image that was selected 
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		this.maskedSrcImage = maskedSrcImage;
	}

	/**
	 * 
	 * @return source image
	 */
	public BufferedImage getSourceImage() {
		return srcImage;
	}

	/**
	 * 
	 * @return mask image
	 */
	public BufferedImage getMaskImage() {
		return maskImage;
	}
	
	/**
	 * 
	 * @return masked source image
	 */
	public BufferedImage getMaskedSourceImage() {
		return maskedSrcImage;
	}
}
