/*
 * ImageFrame.java
 *
 * Created on 2007-10-21, 15:29:35
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ui.containers.ImageFrameSelection;
import ui.containers.ImageFramesContainer;
import ui.containers.ImageHolder;
import ui.containers.PreviewContainer;
import ui.containers.SelectionHolder;
import ui.containers.WindowItem;
import ui.events.ImageFrameEvents;
import ui.events.ImageFrameMouseListener;
import ui.events.ImageFrameMouseMotionListener;
import ui.events.SelectionGhostDropEvent;

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
	private ImageHolder image;
	private boolean modified = false;
	private ImageFramesContainer container;
	private WindowItem menuitem;
	private final ImageFrameSelection selection = new ImageFrameSelection();

	/**
	 * Contructeur
	 * 
	 * @param img
	 *            est l'image holder (original+preview)
	 * @param container
	 *            est le container pour les images frames (pointer)
	 * @param preview
	 *            est le container du preview (pointer)
	 */
	public ImageFrame(ImageHolder img, ImageFramesContainer container, PreviewContainer preview) {

		// passe param à la classe parent
		super(img.getFilename(), true, true, true);

		this.image = img;
		this.container = container;

		// creer un panel pour contenir l'image
		JPanel panel = new JPanel();
		JLabel label = new JLabel();

		// etre sur qu'il y a aucune bordure
		panel.setBorder(BorderFactory.createEmptyBorder());
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setIcon(new ImageIcon(image.getOriginal()));

		// ajoute le panel à l'imageframe
		panel.add(label);
		add(panel);

		// set les paramètres du imageframe
		setSize(image.getOriginal().getWidth() + 30, image.getOriginal().getHeight() + 60);
		setVisible(true);
		setMaximizable(false);
		setAutoscrolls(true);
		setFocusable(true);

		// demande le focus au desktop pour afficher le nouveau imageframe à
		// l'avant de tout les autres
		requestFocus();

		//ajouter les événements de la souris (mouseevent, dradndrop)
		//TODO get rid of preview
		ImageFrameEvents ife = new ImageFrameEvents(this);
		addInternalFrameListener(ife);
		
		GhostGlassPane glassPane = (GhostGlassPane) UIApp.getApplication().getMainFrame().getGlassPane();
		
		ImageFrameMouseListener mouseListener = new ImageFrameMouseListener(glassPane, image.getOriginal(), selection);
		label.addMouseListener(mouseListener);
		
		ImageFrameMouseMotionListener mouseMotionListener = new ImageFrameMouseMotionListener(glassPane, selection);
		label.addMouseMotionListener(mouseMotionListener);
		
		UIView appView = (UIView) UIApp.getApplication().getMainView();
		AbstractGhostDropManager dropListener = new AbstractGhostDropManager(appView.getSelectionBrowser()) {
			public void ghostDropped(GhostDropEvent e) {
				if(isInTarget(getTranslatedPoint(e.getDropLocation()))) {
					if(e instanceof SelectionGhostDropEvent) {
						SelectionGhostDropEvent selectionEvent = (SelectionGhostDropEvent) e;
						System.out.println("Dropped " + selectionEvent.getMaskImage());
						
						SelectionBrowser selBrowser = (SelectionBrowser) component;
						SelectionHolder holder = new SelectionHolder(selectionEvent.getSourceImage(), selectionEvent.getMaskImage());
						selBrowser.addImage(holder);
					}
				}
			}
		};
		mouseListener.addGhostDropListener(dropListener);
	}

	public boolean isModified() {
		return modified;
	}
	
	public ImageHolder getImageHolder() {
		return image;
	}

	/**
	 * Événement lors de la fermeture du image frame
	 */
	public void close() {
		UIView.WindowsMenu.remove(menuitem);
		container.remove(this);
	}

	public WindowItem getMenuItem() {
		return menuitem;
	}

	public void setMenuItem(WindowItem item) {
		menuitem = item;
	}

	public void windowStateChanged(WindowEvent we) {
		if(we.getNewState() == WindowEvent.WINDOW_ACTIVATED) {
			System.out.println("Bingo !!!");
		}
	}
}