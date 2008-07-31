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

import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;

import ca.etsmtl.photomontage.ui.ImageFrame;


/**
 * Classe WindowItem est utilisé pour le lien avec le JMenuItem du menu Window
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 * 
 */
public class WindowItem extends JMenuItem {

	/**
	 * generated id
	 */
	private static final long serialVersionUID = -7458514442755943962L;

	private ImageFrame imageframe;

	private JDesktopPane mdi;

	/**
	 * Constructeur
	 * 
	 * @param imageframe
	 *            est l'image
	 * @param mdi
	 *            est la composante qui contient le desktop manager
	 */
	public WindowItem(ImageFrame imageframe, JDesktopPane mdi) {
		super();

		this.mdi = mdi;
		this.imageframe = imageframe;
		setText(imageframe.getTitle());

		addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				changeWindow();
			}
		});
	}

	/**
	 * Changer le focus sur l'image
	 */
	protected void changeWindow() {
		imageframe.requestFocus();
		mdi.getDesktopManager().activateFrame(imageframe);
	}
}
