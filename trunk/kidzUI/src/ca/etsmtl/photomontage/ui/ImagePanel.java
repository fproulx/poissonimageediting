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
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author fproulx
 *
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 6195069328645116258L;
	private BufferedImage img;
 
    /**
     * @param img
     */
    public ImagePanel(BufferedImage img) {
    	setImage(img);
    }
 
    /**
     * 
     * @return image
     */
    public BufferedImage getImage() {
		return img;
	}

    /**
     * Set image
     * @param img
     */
	public void setImage(BufferedImage img) {
		if(this.img != null) {
			synchronized(this.img) {
				this.img = img;
			}
		}
		else {
			this.img = img;
		}
		
		setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		repaint();
	}	

	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    }
 
}