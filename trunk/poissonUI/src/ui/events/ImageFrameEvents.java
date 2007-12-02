/*
 * ImageFrameEvent.java
 *
 * Created on Oct 28, 2007, 12:59:46 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui.events;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.developpez.gfx.swing.drag.AbstractGhostDropManager;
import com.developpez.gfx.swing.drag.GhostDropEvent;

import ui.ImageFrame;
import ui.SelectionBrowser;
import ui.UIApp;
import ui.UIView;
import ui.containers.PreviewContainer;
import ui.containers.SelectionHolder;

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