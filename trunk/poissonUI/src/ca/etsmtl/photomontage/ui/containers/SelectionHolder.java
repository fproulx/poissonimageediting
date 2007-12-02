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

	private BufferedImage srcImage, scaledSrcImage, maskImage;

	public SelectionHolder(BufferedImage srcImage, BufferedImage maskImage) {
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		//TODO: tight coupling is satan's brother -----> no currentSize icite
		this.scaledSrcImage = createScaledImage(SelectionBrowser.currentSize);
	}

	/**
	 * This method returns an image with the specified width. It finds the
	 * pre-scaled size with the closest/larger width and scales down from it, to
	 * provide a fast and high-quality scaled version at the requested size.
	 */
	private BufferedImage createScaledImage(int width) {
		if(srcImage.getWidth() > width) {
			float scaleFactor = (float) width / srcImage.getWidth();
			int scaledH = (int) (srcImage.getHeight() * scaleFactor);

			BufferedImage img = new BufferedImage(width, scaledH, srcImage
					.getType());
			Graphics2D g2d = img.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(srcImage, 0, 0, width, scaledH, null);
			g2d.dispose();

			return img;
		}
		return srcImage;
	}

	public BufferedImage getMaskImage() {
		return maskImage;
	}

	/**
	 * 
	 * @return l'image redimensionné
	 */
	public BufferedImage getScaledSourceImage() {
		return scaledSrcImage;
	}

	/**
	 * 
	 * @return l'image original
	 */
	public BufferedImage getSourceImage() {
		return srcImage;
	}
}
