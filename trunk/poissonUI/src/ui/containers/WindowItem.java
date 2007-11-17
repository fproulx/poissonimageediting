package ui.containers;

import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;

import ui.ImageFrame;

public class WindowItem extends JMenuItem {

	/**
	 * generated id
	 */
	private static final long serialVersionUID = -7458514442755943962L;
	
	private ImageFrame imageframe;
	
	private JDesktopPane mdi;
	
	/**
	 * Constructeur
	 * 
	 * @param imageframe est l'image
	 * @param mdi est la composante qui contient le desktop manager 
	 */
	public WindowItem(ImageFrame imageframe, JDesktopPane mdi) {
		super();
		
		this.mdi = mdi;
		this.imageframe = imageframe;
		setText(imageframe.getTitle());
		
		addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {				
				changeWindow();
			}
		});
	}
	
	/**
	 * Changer le focus sur l'image
	 */
	protected void changeWindow() {
		imageframe.requestFocus();		
		mdi.getDesktopManager().activateFrame(imageframe);
	}
}
