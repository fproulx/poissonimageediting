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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;


import ca.etsmtl.photomontage.ui.containers.ImageFrameSelection;

import com.developpez.gfx.swing.drag.GhostGlassPane;
import com.developpez.gfx.swing.drag.GhostMotionAdapter;

/**
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */
public class ImageFrameMouseMotionListener extends GhostMotionAdapter {
	
	private ImageFrameSelection selection;
	
	/**
	 * Constructor
	 * @param glassPane
	 */
	public ImageFrameMouseMotionListener(GhostGlassPane glassPane) {
		super(glassPane);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor
	 * @param glassPane
	 * @param selection
	 */
	public ImageFrameMouseMotionListener(GhostGlassPane glassPane, ImageFrameSelection selection) {
		super(glassPane);
		// TODO Auto-generated constructor stub
		
		this.selection = selection;
	}
	
	public void mouseDragged(MouseEvent e) {
		if (selection.isSelectionMode() == true) {
			if (!selection.getPoints().contains(e.getPoint())) {
	
				// Draw a line from the last point to the new one which is the cutest
				Point last = selection.getPoints().get(selection.getPoints().size()-1);
				
				selection.getPoints().add(e.getPoint());
				Graphics g = e.getComponent().getGraphics();
				g.drawLine(e.getPoint().x, e.getPoint().y, last.x, last.y);
	
			}
		}
		else
			super.mouseDragged(e);
	}


}
