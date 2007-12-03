/*
 * ImageHolder.java
 * 
 * Created on Oct 28, 2007, 11:57:46 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.etsmtl.photomontage.ui.containers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import ca.etsmtl.photomontage.ui.ImageBrowser;


/**
 * Classe ImageHolder contient l'image original ainsi que le preview
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
	 * @return l'image original
	 */
	public BufferedImage getImage() {
		return original;
	}

	/**
	 * 
	 * @return l'image redimensionn�
	 */
	public BufferedImage getScaledImage() {
		return scaledImage;
	}
}
