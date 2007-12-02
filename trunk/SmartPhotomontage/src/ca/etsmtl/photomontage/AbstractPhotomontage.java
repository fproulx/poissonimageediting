/*
 * Seamless Image Cloning Tools
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

package ca.etsmtl.photomontage;

import java.awt.Point;
import java.awt.image.BufferedImage;

import ca.etsmtl.photomontage.exceptions.ComputationException;

/**
 * This is a generalized abstract class to every Photomontage implementation
 * @author fproulx
 *
 */
public abstract class AbstractPhotomontage {
	/**
	 * The Source, Mask and Destination images which will be used to create the {@code Photomontage}.
	 */
	protected final BufferedImage srcImage, maskImage, destImage;
	/**
	 * The target position of the cloned image in the destination image.  
	 */
	protected final Point destPosition;
	
	/**
	 * Constructs a {@code Photomontage} and sets the required fields to compute the resulting image.
	 * 
	 * @param srcImage The Source image (to be seamlessly cloned in the {@code Photomontage}).
	 * @param maskImage The Mask image used to specify the pixels of the Source image to be used.
	 * @param destImage The Destination image onto which the Source image will be cloned.
	 * @param destPosition The target position of the cloned image.
	 */
	public AbstractPhotomontage(final BufferedImage srcImage, final BufferedImage maskImage, final BufferedImage destImage, final Point destPosition) {
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		this.destImage = destImage;
		this.destPosition = destPosition;
	}
	
	/**
	 * Creates a photomontage
	 * @return The computed seamlessly cloned image.
	 * @throws ComputationException
	 */
	public abstract BufferedImage createPhotomontage() throws ComputationException;
}
