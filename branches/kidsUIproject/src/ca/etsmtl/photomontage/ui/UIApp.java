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

import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import net.sourceforge.napkinlaf.*;

/**
 * The main class of the application.
 */
public class UIApp extends SingleFrameApplication {

	// Flag that change to look and feel to give it a more prototyping feeling
	private final boolean prototypingUI = false;
	
	/**
	 * At startup create and show the main frame of the application.
	 */
	@Override
	protected void startup() {
		
		if (prototypingUI) {
			initializeNapkinLookAndFeel(); 
		}
		
		show(new UIView(this));
	}

	private void initializeNapkinLookAndFeel() {
		// Load napkin look and feel to enforce a prototyping feeling to our app
		// see http://headrush.typepad.com/creating_passionate_users/2006/12/dont_make_the_d.html
		// for the rationale behind this.
		LookAndFeel laf; 
		laf = new NapkinLookAndFeel(); 
		try {
			UIManager.setLookAndFeel(laf);
			
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is to initialize the specified window by injecting resources.
	 * Windows shown in our application come fully initialized from the GUI
	 * builder, so this additional configuration is not needed.
	 */
	@Override
	protected void configureWindow(java.awt.Window root) {
	}

	/**
	 * A convenient static getter for the application instance.
	 * 
	 * @return the instance of UIApp
	 */
	public static UIApp getApplication() {
		return Application.getInstance(UIApp.class);
	}
	
	/**
	 * @param t
	 */
	public static void showException(final Throwable t) {
		JOptionPane.showMessageDialog(null, t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Main method launching the application.
	 * @param args 
	 */
	public static void main(String[] args) {
		launch(UIApp.class, args);
	}
}
