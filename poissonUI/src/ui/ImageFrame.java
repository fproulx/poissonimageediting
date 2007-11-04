/*
 * ImageFrame.java
 *
 * Created on 2007-10-21, 15:29:35
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
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

	public ImageFrame(ImageHolder img,
			ImageFramesContainer container, PreviewContainer preview) {
		super(img.getFilename(), true, true, true);

		this.image = img;
		this.container = container;

		JPanel panel = new JPanel();
		JLabel label = new JLabel();

		//etre sur qu'il y a aucune bordure
		panel.setBorder(BorderFactory.createEmptyBorder());
		label.setBorder(BorderFactory.createEmptyBorder());
		label.setIcon(new ImageIcon(image.getOriginal()));

		panel.add(label);
		add(panel);
		
		setSize(image.getOriginal().getWidth()+30, image.getOriginal().getHeight()+60);
		setVisible(true);
		setAutoscrolls(true);
		setFocusable(true);
		
		requestFocus();

		addInternalFrameListener(new ImageFrameEvents(this, preview));

		label.addMouseListener(new ImageFrameMouseEvents());
		label.addMouseMotionListener(new ImageFrameMouseEvents());
		
		System.out.println("ImageFrame.ImageFrame "
				+ image.getOriginal().getWidth() + "x"
				+ image.getOriginal().getHeight());
	}

	public boolean isModified() {
		return modified;
	}

	public BufferedImage getBufferedImage() {
		return image.getOriginal();
	}

	public ImageHolder getImage() {
		return image;
	}

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
}