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

package ca.etsmtl.photomontage.ui.controllers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ca.etsmtl.photomontage.ui.ImageBrowser;


/**
 * Classe MenuController s'occupe des actions des menus
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class MenuController {

	private static final int NEW_DOCUMENT_DEFAULT_HEIGHT = 300;
	private static final int NEW_DOCUMENT_DEFAULT_WIDTH = 400;

	/**
	 * Ouverture d'une image
	 * 
	 * @param browser est le conteneur d'images pour l'ajouter
	 * @return l'image ouverte
	 */
	public BufferedImage openImage(ImageBrowser browser) {

		BufferedImage image = null;

		//creation du filechooser pour ouvrir une image
		String filename = File.separator + "Users"; //TODO Have it open in $home
		JFileChooser fc = new JFileChooser(new File(filename));
		int returnValue = fc.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			
			try {
				//Lire l'image avec un ImageIO et l'ajouter au image browser
				
				File file = fc.getSelectedFile();
				if (file.exists() && file.canRead() && file.isFile()) {
					image = ImageIO.read(file);
					browser.addImage(image, file.getCanonicalPath());
				}
			} catch (Exception e) {
				//message d'erreur
				JOptionPane.showMessageDialog(null,
					    "Error while open the image.",
					    "Error while opening",
					    JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null,
				    "Vous avez cancelé l'opération",
				    "Ouvrir",
				    JOptionPane.WARNING_MESSAGE);
		}

		return image;
	}
	
	/**
	 * Nouvelle image
	 * 
	 * @param browser est le conteneur d'images pour l'ajouter
	 * @return la nouvelle image
	 */
	public BufferedImage newImage(ImageBrowser browser) {

		// Create a new image using default width and height
		BufferedImage image = new BufferedImage(NEW_DOCUMENT_DEFAULT_WIDTH,NEW_DOCUMENT_DEFAULT_HEIGHT,BufferedImage.TYPE_INT_RGB);
	
		// Fill image with white
		// TODO there is probably a better way to do it, anyone?
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, NEW_DOCUMENT_DEFAULT_WIDTH, NEW_DOCUMENT_DEFAULT_HEIGHT);
		
		// Add to image browser
		// TODO it should be added directly to workspace probably
		browser.addImage(image, null);
		
		return image;
	}

	/**
	 * Sauvegarde de l'image sur le disque
	 * 
	 * @param image a sauvegarder sur le disque
	 * @return path ou l'image a ete sauvegarder
	 */
	public String saveFile(BufferedImage image) {
		//creation du filechooser pour sauvegarder une image
		String filename = File.separator + "Users";
		JFileChooser fc = new JFileChooser(filename);
		
		//changer le type de dialog pour la sauvegarde
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Untitled");

		//code de retour
		int returnVal = fc.showSaveDialog(null);

		try {
			//si ok sauvegarder l'image avec le nom
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File myfile = fc.getSelectedFile();
				ImageIO.write(image, "PNG", myfile);
				return myfile.getAbsolutePath();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
				    "Error while saving the new image.",
				    "Error while saving",
				    JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} 
		
		return "";
	}
}