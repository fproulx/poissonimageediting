/*
 * PreviewContainer.java
 * 
 * Created on Oct 28, 2007, 1:46:07 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.etsmtl.photomontage.ui.containers;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 * Classe PreviewContainer contient l'image redimensionné
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class PreviewContainer extends Observable {

	private BufferedImage scaledImage;

	/**
	 * Constructeur
	 */
	public PreviewContainer() {

	}

	public BufferedImage getScaledImage() {
		return scaledImage;
	}

	/**
	 * Met à jour la composante qui contient le "preview"
	 * 
	 * @param scaledImage est l'image "resizé"
	 */
	public void update(BufferedImage scaledImage) {
		System.out.println("PreviewContainer.update");

		this.scaledImage = scaledImage;
		setChanged();
		notifyObservers();
	}
}
