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

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the state of the selection and the points that makes this selection  
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel <lebel.kim@gmail.com>, Jean-Philippe Plante <jphilippeplante@gmail.com>, Francois Proulx <francois.proulx@gmail.com> 
 * TODO improve this class's friendliness with ImageFrameMouseListener 
 */
public class ImageFrameSelection {

	private boolean mode = true;
	private List<Point> points = new ArrayList<Point>();
	
	/**
	 * 
	 * @return selection mode
	 */
	public boolean isSelectionMode() {
		return mode;
	}
	
	/**
	 * Set selection mode
	 * @param mode
	 */
	public synchronized void setMode(boolean mode) {
		this.mode = mode;
	}
	
	/**
	 * 
	 * @return points
	 */
	public List<Point> getPoints() {
		return points;
	}
	
	/**
	 * Set points
	 * @param points
	 */
	public synchronized void setPoints(List<Point> points) {
		this.points = points;
	}
}
