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

package ca.etsmtl.photomontage.ui;

import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;

import ca.etsmtl.photomontage.ui.containers.ImageFrameSelection;
import ca.etsmtl.photomontage.ui.containers.ImageFramesContainer;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;
import ca.etsmtl.photomontage.ui.containers.WindowItem;
import ca.etsmtl.photomontage.ui.events.ImageFrameEvents;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseListener;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseMotionListener;
import ca.etsmtl.photomontage.ui.events.SelectionGhostDropEvent;

import com.developpez.gfx.swing.drag.AbstractGhostDropManager;
import com.developpez.gfx.swing.drag.GhostDropEvent;
import com.developpez.gfx.swing.drag.GhostGlassPane;

/**
 * Classe ImageFrame contient l'image de travail qui sera dans le desktop
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageFrame extends JInternalFrame {

	/**
	 * generated serial uid
	 */
	private static final long serialVersionUID = -2677920466179585697L;
	private ImageHolder imageHolder;
	private ImageFramesContainer container;
	private WindowItem menuitem;
	private final ImageFrameSelection selection = new ImageFrameSelection();
	private boolean modificationStatus;
	private ImagePanel imagePanel;

	/**
	 * Contructeur
	 * 
	 * @param holder
	 *            est l'image holder (original)
	 * @param container
	 *            est le container pour les images frames (pointer)
	 */
	public ImageFrame(ImageHolder holder, ImageFramesContainer container) {

		// passe param a la classe parent
		super(holder.getFilename(), true, true, true);

		this.imageHolder = holder;
		this.container = container;

		// Create and add the panel that will contain the image
		imagePanel = new ImagePanel(imageHolder.getImage()); 
		add(imagePanel);

		// set les parametres du imageframe
		//setSize(imageHolder.getImage().getWidth(), imageHolder.getImage().getHeight());
		setVisible(true);
		setMaximizable(false);
		setResizable(false);
		setFocusable(true);
		
		pack();

		// demande le focus au desktop pour afficher le nouveau imageframe �
		// l'avant de tout les autres
		requestFocus();

		//ajouter les evenements de la souris (mouseevent, dradndrop)
		ImageFrameEvents ife = new ImageFrameEvents(this);
		addInternalFrameListener(ife);
		
		GhostGlassPane glassPane = (GhostGlassPane) UIApp.getApplication().getMainFrame().getGlassPane();
		
		ImageFrameMouseListener mouseListener = new ImageFrameMouseListener(glassPane, imageHolder.getImage(), selection);
		imagePanel.addMouseListener(mouseListener);
		
		ImageFrameMouseMotionListener mouseMotionListener = new ImageFrameMouseMotionListener(glassPane, selection);
		imagePanel.addMouseMotionListener(mouseMotionListener);
		
		UIView appView = (UIView) UIApp.getApplication().getMainView();
		AbstractGhostDropManager dropListener = new AbstractGhostDropManager(appView.getSelectionBrowser()) {
			public void ghostDropped(GhostDropEvent e) {
				if(isInTarget(getTranslatedPoint(e.getDropLocation()))) {
					if(e instanceof SelectionGhostDropEvent) {
						SelectionGhostDropEvent selectionEvent = (SelectionGhostDropEvent) e;
						SelectionBrowser selBrowser = (SelectionBrowser) component;
						SelectionHolder holder = new SelectionHolder(selectionEvent.getSourceImage(), selectionEvent.getMaskImage(), selectionEvent.getMaskedSourceImage());
						selBrowser.addImage(holder);
					}
				}
			}
		};
		mouseListener.addGhostDropListener(dropListener);
	}

	/**
	 * 
	 * @param status of the image
	 */
	public void setModified(boolean status) {
		this.modificationStatus = status;
	}
	
	/**
	 * 
	 * @return if the image is modified
	 */
	public boolean isModified() {
		return modificationStatus;
	}
	
	/**
	 * 
	 * @return the image holder
	 */
	public ImageHolder getImageHolder() {
		return imageHolder;
	}

	/**
	 * Evenement lors de la fermeture du image frame
	 */
	public void close() {
		container.remove(this);
	}

	/**
	 * 
	 * @return the menu item in window menu
	 */
	public WindowItem getMenuItem() {
		return menuitem;
	}

	/**
	 * Set menu item
	 * @param item is the new window item
	 */
	public void setMenuItem(WindowItem item) {
		menuitem = item;
	}

	/**
	 * Set the new image holder and update image in swing
	 * @param newImageHolder
	 */
	public void setImageHolder(ImageHolder newImageHolder) {
		// Change the image in a thread-safe manner.
		if(imageHolder != null) {
			synchronized(imageHolder) {
				imageHolder = newImageHolder;
			}
		}
		
		// Ask Swing to redraw the component that contains the image later
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				imagePanel.setImage(imageHolder.getImage());
			}	
		});
	}
}