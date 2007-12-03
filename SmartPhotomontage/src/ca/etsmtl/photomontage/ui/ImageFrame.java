/*
 * ImageFrame.java
 *
 * Created on 2007-10-21, 15:29:35
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ca.etsmtl.photomontage.ui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ca.etsmtl.photomontage.ui.containers.ImageFrameSelection;
import ca.etsmtl.photomontage.ui.containers.ImageFramesContainer;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.PreviewContainer;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;
import ca.etsmtl.photomontage.ui.containers.WindowItem;
import ca.etsmtl.photomontage.ui.events.ImageFrameEvents;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseListener;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseMotionListener;
import ca.etsmtl.photomontage.ui.events.SelectionGhostDropEvent;

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
	private ImageHolder imageHolder;
	private ImageFramesContainer container;
	private WindowItem menuitem;
	private final ImageFrameSelection selection = new ImageFrameSelection();
	private boolean modificationStatus;
	private JLabel label;

	/**
	 * Contructeur
	 * 
	 * @param holder
	 *            est l'image holder (original+preview)
	 * @param container
	 *            est le container pour les images frames (pointer)
	 * @param preview
	 *            est le container du preview (pointer)
	 */
	public ImageFrame(ImageHolder holder, ImageFramesContainer container, PreviewContainer preview) {

		// passe param � la classe parent
		super(holder.getFilename(), true, true, true);

		this.imageHolder = holder;
		this.container = container;

		// creer un panel pour contenir l'image
		JPanel panel = new JPanel();
		label = new JLabel();

		// etre sur qu'il y a aucune bordure
		panel.setBorder(BorderFactory.createEmptyBorder());
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setIcon(new ImageIcon(imageHolder.getImage()));

		// ajoute le panel � l'imageframe
		panel.add(label);
		add(panel);

		// set les param�tres du imageframe
		setSize(imageHolder.getImage().getWidth() + 30, imageHolder.getImage().getHeight() + 60);
		setVisible(true);
		setMaximizable(false);
		setAutoscrolls(true);
		setFocusable(true);

		// demande le focus au desktop pour afficher le nouveau imageframe �
		// l'avant de tout les autres
		requestFocus();

		//ajouter les �v�nements de la souris (mouseevent, dradndrop)
		//TODO get rid of preview
		ImageFrameEvents ife = new ImageFrameEvents(this);
		addInternalFrameListener(ife);
		
		GhostGlassPane glassPane = (GhostGlassPane) UIApp.getApplication().getMainFrame().getGlassPane();
		
		ImageFrameMouseListener mouseListener = new ImageFrameMouseListener(glassPane, imageHolder.getImage(), selection);
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

	/**
	 * 
	 * @param status of the image
	 */
	public void setModified(boolean status) {
		this.modificationStatus = status;
	}
	
	/**
	 * 
	 * @return if the image is modified
	 */
	public boolean isModified() {
		return modificationStatus;
	}
	
	/**
	 * 
	 * @return the image holder
	 */
	public ImageHolder getImageHolder() {
		return imageHolder;
	}

	/**
	 * Evenement lors de la fermeture du image frame
	 */
	public void close() {
		UIView.WindowsMenu.remove(menuitem);
		container.remove(this);
	}

	/**
	 * 
	 * @return the menu item in window menu
	 */
	public WindowItem getMenuItem() {
		return menuitem;
	}

	/**
	 * Set menu item
	 * @param item is the new window item
	 */
	public void setMenuItem(WindowItem item) {
		menuitem = item;
	}

	/**
	 * Set the new image holder and update image in swing
	 * @param newImageHolder
	 */
	public void setImageHolder(ImageHolder newImageHolder) {
		// Change the image in a thread-safe manner.
		if(imageHolder != null) {
			synchronized(imageHolder) {
				imageHolder = newImageHolder;
			}
		}
		
		// Ask Swing to redraw the component that contains the image later
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				label.setIcon(new ImageIcon(imageHolder.getImage()));
			}	
		});
	}
}