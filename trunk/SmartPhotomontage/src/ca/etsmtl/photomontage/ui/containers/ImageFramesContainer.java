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

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import ca.etsmtl.photomontage.ui.ImageFrame;


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
	 * V�rifie si le frame est contenu dans la liste
	 * @param frame
	 * @return si la fenetre de l'image est dans la liste
	 */
	public boolean contains(ImageFrame frame) {
		// check if its in the imageframe list
		for (ImageFrame myframe : imageFrames) {
			if (frame.getImageHolder().hashCode() == myframe.getImageHolder().hashCode()) {
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
