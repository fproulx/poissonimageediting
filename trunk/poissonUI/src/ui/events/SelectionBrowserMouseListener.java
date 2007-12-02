package ui.events;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import com.developpez.gfx.swing.drag.GhostDropAdapter;
import com.developpez.gfx.swing.drag.GhostGlassPane;

//TODO: Fix bug disappearing lines ?!
/**
 *
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class SelectionBrowserMouseListener extends GhostDropAdapter {
	
	// Image that represents the selected part of the image
	private BufferedImage srcImage;
	
	public SelectionBrowserMouseListener(GhostGlassPane glassPane, BufferedImage img) {
		super(glassPane, null);

		this.srcImage = img;
	}
	
	public void mousePressed(MouseEvent e) {

		Component c = e.getComponent();

        glassPane.setVisible(true);

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setImage(srcImage);
        glassPane.repaint();
	}

	public void mouseReleased(MouseEvent e) {

		Component c = e.getComponent();

		// Ghostly drag and drop
        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);

        Point eventPoint = (Point) p.clone();
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setVisible(false);
        glassPane.setImage(null);

        //TODO fix this!
        //fireGhostDropEvent(new SelectionGhostDropEvent(srcImage, maskImage, eventPoint));
	}
}