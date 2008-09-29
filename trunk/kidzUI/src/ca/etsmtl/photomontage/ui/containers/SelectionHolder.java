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

package ca.etsmtl.photomontage.ui.containers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import ca.etsmtl.photomontage.ui.SelectionBrowser;


/**
 * SelectionHolder contains the mask, scaled version 
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
//TODO: Consider refactoring with ImageHolder
public class SelectionHolder {

	private BufferedImage srcImage, scaledSrcImage, maskImage, maskedSrcImage;

	/**
	 * @param srcImage
	 * @param maskImage
	 * @param maskedSrcImage 
	 */
	public SelectionHolder(BufferedImage srcImage, BufferedImage maskImage, BufferedImage maskedSrcImage) {
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		this.maskedSrcImage = maskedSrcImage;
		//TODO: tight coupling is satan's brother -----> no currentSize icite
		this.scaledSrcImage = createScaledImage(SelectionBrowser.currentSize);
	}

	/**
	 * This method returns an image with the specified width. It finds the
	 * pre-scaled size with the closest/larger width and scales down from it, to
	 * provide a fast and high-quality scaled version at the requested size.
	 */
	private BufferedImage createScaledImage(int width) {
		if(maskedSrcImage.getWidth() > width) {
			float scaleFactor = (float) width / maskedSrcImage.getWidth();
			int scaledH = (int) (maskedSrcImage.getHeight() * scaleFactor);

			BufferedImage img = new BufferedImage(width, scaledH, maskedSrcImage.getType());
			Graphics2D g2d = img.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(maskedSrcImage, 0, 0, width, scaledH, null);
			g2d.dispose();

			return img;
		}
		return maskedSrcImage;
	}

	/**
	 * 
	 * @return mask
	 */
	public BufferedImage getMaskImage() {
		return maskImage;
	}
	
	/**
	 * 
	 * @return masked source
	 */
	public BufferedImage getMaskedSourceImage() {
		return maskedSrcImage;
	}

	/**
	 * 
	 * @return l'image redimensionné
	 */
	public BufferedImage getScaledImage() {
		return scaledSrcImage;
	}

	/**
	 * 
	 * @return l'image original
	 */
	public BufferedImage getImage() {
		return srcImage;
	}
}
