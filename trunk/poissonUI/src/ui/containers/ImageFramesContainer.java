/*
 * ImagesContainer.java
 * 
 * Created on 2007-10-22, 11:09:57
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.containers;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import ui.ImageFrame;

/**
 * Classe ImageFramesContainer contient la liste des imageframes
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageFramesContainer extends Observable {

	private List<ImageFrame> imageFrames = new ArrayList<ImageFrame>();

	/**
	 * Ajoute un imageframe dans la liste
	 * @param frame est un imageframe
	 */
	public void add(ImageFrame frame) {
		// add it and notify the observers
		if (!contains(frame) && frame != null) {
			imageFrames.add(frame);
			
			//notify observers
			setChanged();
			notifyObservers(imageFrames);
		}
	}

	/**
	 * Enleve un imageframe dans la liste
	 * @param frame est un imageframe
	 */
	public void remove(ImageFrame frame) {
		if (imageFrames.contains(frame) && frame != null) {
			imageFrames.remove(frame);
			
			//notify observers
			setChanged();
			notifyObservers(imageFrames);
		}
	}

	/**
	 * Vérifie si le frame est contenu dans la liste
	 * @param frame
	 * @return si la fenetre de l'image est dans la liste
	 */
	public boolean contains(ImageFrame frame) {
		// check if its in the imageframe list
		for (ImageFrame myframe : imageFrames) {
			if (frame.getImage().hashCode() == myframe.getImage().hashCode()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return la liste des frames
	 */
	public List<ImageFrame> getFrames() {
		return imageFrames;
	}
}
