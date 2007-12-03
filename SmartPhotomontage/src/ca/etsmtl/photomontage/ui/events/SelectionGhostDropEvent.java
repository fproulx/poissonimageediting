package ca.etsmtl.photomontage.ui.events;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.developpez.gfx.swing.drag.GhostDropEvent;

public class SelectionGhostDropEvent extends GhostDropEvent {
	private final BufferedImage srcImage, maskImage, maskedSrcImage;
	
	public SelectionGhostDropEvent(BufferedImage srcImage, BufferedImage maskImage, BufferedImage maskedSrcImage, Point point) {
		super(null, point);
		
		// Copy a reference to the portion of the image that was selected 
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		this.maskedSrcImage = maskedSrcImage;
	}

	public BufferedImage getSourceImage() {
		return srcImage;
	}

	public BufferedImage getMaskImage() {
		return maskImage;
	}
	
	public BufferedImage getMaskedSourceImage() {
		return maskedSrcImage;
	}
}
