package ca.etsmtl.photomontage.ui.events;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;


import ca.etsmtl.photomontage.ui.containers.ImageFrameSelection;

import com.developpez.gfx.swing.drag.GhostGlassPane;
import com.developpez.gfx.swing.drag.GhostMotionAdapter;

/**
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class ImageFrameMouseMotionListener extends GhostMotionAdapter {
	
	private ImageFrameSelection selection;
	
	public ImageFrameMouseMotionListener(GhostGlassPane glassPane) {
		super(glassPane);
		// TODO Auto-generated constructor stub
	}
	
	public ImageFrameMouseMotionListener(GhostGlassPane glassPane, ImageFrameSelection selection) {
		super(glassPane);
		// TODO Auto-generated constructor stub
		
		this.selection = selection;
	}
	
	public void mouseDragged(MouseEvent e) {
		if (selection.isSelectionMode() == true) {
			if (!selection.getPoints().contains(e.getPoint())) {
	
				// Draw a line from the last point to the new one which is the cutest
				Point last = selection.getPoints().get(selection.getPoints().size()-1);
				
				selection.getPoints().add(e.getPoint());
				Graphics g = e.getComponent().getGraphics();
				g.drawLine(e.getPoint().x, e.getPoint().y, last.x, last.y);
	
			}
		}
		else
			super.mouseDragged(e);
	}


}
