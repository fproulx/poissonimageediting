package ui.dnd;

import javax.swing.TransferHandler;

import ui.events.ImageFrameMouseEvents;

/**
 * TODO drag-n-drop
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 * 
 */
public class ImageSelectionHandler extends TransferHandler {

	/**
	 * generated uid
	 */
	private static final long serialVersionUID = 4033274773315799067L;

	public ImageSelectionHandler(String property, ImageFrameMouseEvents events) {
		super(property);
	}

}
