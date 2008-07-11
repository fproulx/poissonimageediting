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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import ca.etsmtl.photomontage.ui.ImageFrame;
import ca.etsmtl.photomontage.ui.containers.ImageFramesContainer;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;


/**
 * Classe ImageBrowserMouseListener est l'impl�mentation les �v�nements de la
 * souris sur le paneau de l'image browser
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageBrowserMouseListener implements MouseListener {

	private ImageHolder image;

	private ImageFramesContainer container;

	/**
	 * 
	 * @param image est le container
	 * @param container
	 */
	public ImageBrowserMouseListener(ImageHolder image,
			ImageFramesContainer container) {
		this.image = image;
		this.container = container;
	}

	public void mouseClicked(MouseEvent arg0) {
		ImageFrame img = new ImageFrame(image, container);
		container.add(img);
	}

	public void mousePressed(MouseEvent arg0) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseReleased(MouseEvent arg0) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseEntered(MouseEvent arg0) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	public void mouseExited(MouseEvent arg0) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}
}