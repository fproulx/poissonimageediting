package ui.dnd;

import javax.swing.TransferHandler;

import ui.ImageFrameMouseEvents;

public class ImageSelectionHandler extends TransferHandler {

	/**
	 * generated uid
	 */
	private static final long serialVersionUID = 4033274773315799067L;

	public ImageSelectionHandler(String property, ImageFrameMouseEvents events) {
		super(property);
	}

}
