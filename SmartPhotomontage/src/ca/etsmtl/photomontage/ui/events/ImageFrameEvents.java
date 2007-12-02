/*
 * ImageFrameEvent.java
 *
 * Created on Oct 28, 2007, 12:59:46 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.etsmtl.photomontage.ui.events;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import ca.etsmtl.photomontage.ui.ImageFrame;
import ca.etsmtl.photomontage.ui.SelectionBrowser;
import ca.etsmtl.photomontage.ui.UIApp;
import ca.etsmtl.photomontage.ui.UIView;
import ca.etsmtl.photomontage.ui.containers.PreviewContainer;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;

import com.developpez.gfx.swing.drag.AbstractGhostDropManager;
import com.developpez.gfx.swing.drag.GhostDropEvent;


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