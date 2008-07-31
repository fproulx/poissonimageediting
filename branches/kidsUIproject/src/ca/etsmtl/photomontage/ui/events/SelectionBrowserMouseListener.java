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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;

import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.exceptions.InvalidDestinationPositionException;
import ca.etsmtl.photomontage.exceptions.InvalidMaskException;
import ca.etsmtl.photomontage.exceptions.InvalidSourceImageSizeException;
import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;
import ca.etsmtl.photomontage.ui.ImageFrame;
import ca.etsmtl.photomontage.ui.ImagePanel;
import ca.etsmtl.photomontage.ui.SelectionBrowser;
import ca.etsmtl.photomontage.ui.UIApp;
import ca.etsmtl.photomontage.ui.UIView;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;

import com.developpez.gfx.swing.drag.GhostDropAdapter;
import com.developpez.gfx.swing.drag.GhostGlassPane;

//TODO: Fix bug disappearing lines ?!
/**
 *
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class SelectionBrowserMouseListener extends GhostDropAdapter {
	
	// Image that represents the selected part of the image
	private final SelectionHolder selectionHolder;

	/**
	 * @param glassPane
	 * @param holder
	 */
	public SelectionBrowserMouseListener(final GhostGlassPane glassPane, final SelectionHolder holder) {
		super(glassPane, null);
		
		this.selectionHolder = holder;
	}

	public void mousePressed(MouseEvent e) {

		Component c = e.getComponent();

        glassPane.setVisible(true);

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setImage(selectionHolder.getMaskedSourceImage());
        glassPane.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		Component c = e.getComponent();

		// Ghostly drag and drop
        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);

        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setVisible(false);
        glassPane.setImage(null);

        // WARNING !!!! This is a big stinky HACK
        
        // Get ahold of the source and destination components for the drag-drop event
        UIView appView = (UIView) UIApp.getApplication().getMainView();
        JDesktopPane desktop = appView.getImageFramesDesktop();
        SelectionBrowser selectionBrowser = appView.getSelectionBrowser();
        
        // Convert the point relative to the Desktop area
        //Point desktopConvertedPoint = SwingUtilities.convertPoint(selectionBrowser, eventPoint, desktop);
        // find the component that under this point
        //Component component = SwingUtilities.getDeepestComponentAt(desktop, desktopConvertedPoint.x, desktopConvertedPoint.y);
        /*
        SwingUtilities.convertPoint(c, e.getPoint(), destination)
        Point z = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(z, c);
        */
        
        
        Point screenPoint = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(screenPoint, c);
        
        Point desktopPoint = (Point) screenPoint.clone();
        SwingUtilities.convertPointFromScreen(desktopPoint, desktop);
        
        Component component = desktop.findComponentAt(desktopPoint);
        
        //TODO: Fix this ugly ugly ugly hack
        //TODO: this should check if it is actually the right ImagePanel (inside an ImageFrame)
        if(component instanceof ImagePanel) {
        	final ImageFrame frame = (ImageFrame) component.getParent().getParent().getParent().getParent();

        	// convert point relative to the target component
        	final Point dstPoint = (Point) screenPoint.clone();
            SwingUtilities.convertPointFromScreen(dstPoint, component);
            dstPoint.translate(- (selectionHolder.getImage().getWidth() / 2), - (selectionHolder.getImage().getHeight() / 2));
            
        	Executor executor = Executors.newSingleThreadExecutor();
        	executor.execute(new Runnable() {
        		public void run() {
        			// Setup the Poisson solver
        			PoissonPhotomontage photomontage = new PoissonPhotomontage(selectionHolder.getImage(), selectionHolder.getMaskImage(), frame.getImageHolder().getImage(), dstPoint);
        			
        			BufferedImage output = null;
        			try {
        				System.out.println("Starting computation...");
	        			// Do the heavy lifting
	        			long t0 = System.nanoTime();
	        			output = photomontage.createPhotomontage();
	        			long t1 = System.nanoTime();
	        			// 2573864000 ns --> 2.573864 s
	        			System.out.printf("%d ns --> %f s\r\n", t1 - t0, (t1 - t0) / Math.pow(10, 9));
	        			
	        			// Replace the image with the photomontage
	        			ImageHolder newImageHolder = new ImageHolder(output, frame.getImageHolder().getFilename());
	        			frame.setImageHolder(newImageHolder);
	        		
	        		// FIXME exceptions should be translated in PhotoMontage instead of here!
					} catch (final ComputationException e) {
        				SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								UIApp.showException(new ComputationException("Une erreur de calcul s'est produite. Réessayez de nouveau avec un masque différent."));
							}
        				});
					} catch (final InvalidSourceImageSizeException e) {
        				SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								UIApp.showException(new InvalidSourceImageSizeException("Il y a eu un problème avec l'image source, recommencer votre photomontage. Si le problème persiste, contactez les développeurs."));
							}
        				});
					} catch (final InvalidDestinationPositionException e) {
        				SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								UIApp.showException(new InvalidDestinationPositionException("Vous devez déposer votre découpage à l'intérieur de l'image de destination."));
							}
        				});
					} catch (final InvalidMaskException e) {
        				SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								UIApp.showException(new InvalidMaskException("Il y a eu un problème avec le masque de l'image découpée, recommencer votre photomontage. Si le problème persiste, contactez les développeurs."));
							}
        				});
					}
        		}
        	});
        }
   
        //TODO fix this!
        //fireGhostDropEvent(new SelectionGhostDropEvent(srcImage, maskImage, eventPoint));
	}
}