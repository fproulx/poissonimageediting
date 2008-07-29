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

import ca.etsmtl.photomontage.ui.ImageBrowser;


/**
 * Classe ImageHolder contient l'image original
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageHolder {
	private final BufferedImage scaledImage;
	private final BufferedImage original;
	private String filename;

	/**
	 * @param originalImage
	 * @param filename
	 */
	public ImageHolder(final BufferedImage originalImage, final String filename) {
		this.original = originalImage;
		this.filename = filename;
		this.scaledImage = createScaledImage(ImageBrowser.currentSize);
	}

	/**
	 * This method returns an image with the specified width. It finds the
	 * pre-scaled size with the closest/larger width and scales down from it, to
	 * provide a fast and high-quality scaled version at the requested size.
	 * 
	 * @param width of the image you want
	 * @return scaled image
	 */
	public BufferedImage createScaledImage(int width) {
		float scaleFactor = (float) width / original.getWidth();
		int scaledH = (int) (original.getHeight() * scaleFactor);

		BufferedImage img = new BufferedImage(width, scaledH, original.getType());
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(original, 0, 0, width, scaledH, null);
		g2d.dispose();

		return img;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * set filename
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @return l'image originale
	 */
	public BufferedImage getImage() {
		return original;
	}

	/**
	 * 
	 * @return l'image redimensionnée
	 */
	public BufferedImage getScaledImage() {
		return scaledImage;
	}
}
