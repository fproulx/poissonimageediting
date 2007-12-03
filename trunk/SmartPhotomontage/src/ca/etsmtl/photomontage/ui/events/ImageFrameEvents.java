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

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import ca.etsmtl.photomontage.ui.ImageFrame;


/**
 * Classe ImageFrameEvents implements ImageFrame events
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageFrameEvents implements InternalFrameListener {
	private ImageFrame frame;

	/**
	 * Constructor
	 * @param frame
	 */
	public ImageFrameEvents(ImageFrame frame) {
		this.frame = frame;
	}
	
	public void internalFrameClosing(InternalFrameEvent arg0) {
		// TODO ask save
	}

	public void internalFrameClosed(InternalFrameEvent ife) {
		frame.close();
	}
	
	public void internalFrameActivated(InternalFrameEvent arg0) {}
	public void internalFrameDeactivated(InternalFrameEvent arg0) {}
	public void internalFrameOpened(InternalFrameEvent ife) {}
	public void internalFrameIconified(InternalFrameEvent arg0) {}
	public void internalFrameDeiconified(InternalFrameEvent arg0) {}

}