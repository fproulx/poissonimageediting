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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;
import ca.etsmtl.photomontage.ui.events.SelectionBrowserMouseListener;

import com.developpez.gfx.swing.drag.GhostGlassPane;
import com.developpez.gfx.swing.drag.GhostMotionAdapter;


/**
 * 
 * Selection Browser based on ImageBrowser
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class SelectionBrowser extends JComponent {
	private static final long serialVersionUID = 8651024289475405132L;

	Dimension newSize = new Dimension();

	/**
	 * Images list
	 */
	public static List<ImageHolder> images = new ArrayList<ImageHolder>();

	/**
	 * Current size of each image in the image browser
	 */
	public final static int currentSize = 180;
	
	/** Creates a new instance of ImageBrowser */
	public SelectionBrowser() {

		initComponents();
	}
	
	private void initComponents() {
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(new Dimension(currentSize, 0));
	}

	/**
	 * Ajoute une image dans le image browser
	 * 
	 * @param selHold
	 */
	public void addImage(SelectionHolder selHold) {

		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(selHold.getScaledImage()));

		GhostGlassPane glassPane = (GhostGlassPane) UIApp.getApplication().getMainFrame().getGlassPane();
		
		SelectionBrowserMouseListener mouseListener = new SelectionBrowserMouseListener(glassPane, selHold);
		label.addMouseListener(mouseListener);
		
		GhostMotionAdapter mouseMotionListener = new GhostMotionAdapter(glassPane);
		label.addMouseMotionListener(mouseMotionListener);
		
		add(label);
		revalidate();
	}
}