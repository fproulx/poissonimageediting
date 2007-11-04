package ui;

import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;

public class WindowItem extends JMenuItem {

	/**
	 * generated id
	 */
	private static final long serialVersionUID = -7458514442755943962L;
	
	private ImageFrame imageframe;
	
	private JDesktopPane mdi;
	
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
	
	protected void changeWindow() {
		imageframe.requestFocus();		
		mdi.getDesktopManager().activateFrame(imageframe);		
		
		System.out.println("request focus");
	}
}
