/*
 * ImageHolder.java
 * 
 * Created on Oct 28, 2007, 11:57:46 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.containers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import ui.ImageBrowser;

/**
 * Classe ImageHolder contient l'image original ainsi que le preview
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageHolder {

	private BufferedImage scaledImage;

	private BufferedImage original;
	
	private String filename = "Untitled";

	public ImageHolder(BufferedImage originalImage, String filename) {
		this.original = originalImage;
		this.filename = filename;
		this.scaledImage = getImage(ImageBrowser.currentSize);
	}

	/**
	 * 
	 * @return l'image redimensionné
	 */
	public BufferedImage getScaledImage() {
		return scaledImage;
	}

	/**
	 * 
	 * @return l'image original
	 */
	public BufferedImage getOriginal() {
		return original;
	}

	/**
	 * This method returns an image with the specified width. It finds the
	 * pre-scaled size with the closest/larger width and scales down from it, to
	 * provide a fast and high-quality scaled version at the requested size.
	 */
	public BufferedImage getImage(int width) {
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
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
