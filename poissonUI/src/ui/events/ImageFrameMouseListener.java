package ui.events;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ui.containers.Selection;

import com.developpez.gfx.swing.drag.GhostDropAdapter;
import com.developpez.gfx.swing.drag.GhostDropEvent;
import com.developpez.gfx.swing.drag.GhostGlassPane;

/**
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class ImageFrameMouseListener extends GhostDropAdapter {
	private Selection selection;
	
	// Image that represents the selected part of the image
	private BufferedImage srcImage;
	
	// Image that represents the actual mask (only composed of black and white)
	private BufferedImage maskImage;
	
	// Original image
	private BufferedImage origImage;
	
	// TODO Original image with the selection line drawn on it
	private BufferedImage origPlusSelectionImage;

	//TODO: remove image --> change vers mask
	public ImageFrameMouseListener(GhostGlassPane glassPane, BufferedImage img, Selection selection) {
		super(glassPane, null);
		// TODO Auto-generated constructor stub
		
		this.selection = selection;
		this.origImage = img;
	}
	
	public void mousePressed(MouseEvent e) {

		System.out.println("Selection Mode is: "+selection.isSelectionMode());
		
		// We are in selection mode
		if (selection.isSelectionMode() == true) {
			List<Point> points = selection.getPoints();
			
			points.clear();
			System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
			points.add(e.getPoint());
			Graphics g = e.getComponent().getGraphics();
		}
		else {
			Component c = e.getComponent();

	        glassPane.setVisible(true);

	        Point p = (Point) e.getPoint().clone();
	        SwingUtilities.convertPointToScreen(p, c);
	        SwingUtilities.convertPointFromScreen(p, glassPane);

	        glassPane.setPoint(p);
	        glassPane.setImage(srcImage);
	        glassPane.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {

		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
		System.out.println("State selection mode: "+selection.isSelectionMode());
		List<Point> points = selection.getPoints();
		
		if (selection.isSelectionMode() == true) {	
			// Finish the selection by drawing a line between the first point and the point where the mouse was released
			Graphics g = e.getComponent().getGraphics();
			Point first = points.get(0);
			points.add(e.getPoint());
			points.add(first);
			g.drawLine(e.getPoint().x, e.getPoint().y, first.x, first.y);

			// Find the upperleft pixel of the selection
			Point topleft = (Point) first.clone();
			for(Point p:points) {
				if (p.x < topleft.x) {
					topleft.x = p.x;
				}
				if (p.y < topleft.y) {
					topleft.y = p.y;
				}
			}
			
			// Construct a polygon from the series of points and fill it
			// Note: this polygon's coordinates are related to the original image
			Polygon poly = new Polygon();
			for(Point p: points) {
				poly.addPoint(p.x - topleft.x, p.y - topleft.y);
			}

			
			// Alter the polygon's points to be related to the extracted image with the mask
			// FIXME if there is only a click and release, there is no points this sucks throw Exception 
			maskImage = new BufferedImage(poly.getBounds().width, poly.getBounds().height,
			BufferedImage.TYPE_INT_ARGB);
			
			srcImage = new BufferedImage(poly.getBounds().width, poly.getBounds().height,
					BufferedImage.TYPE_INT_ARGB);

			
			// MASK: Fill bgcolor with translucent/black then fill polygon in opaque/white
			Graphics2D graphMask = (Graphics2D) maskImage.getGraphics();
			graphMask.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
			// TODO use Color constants after merging with the Poisson part
			graphMask.setColor(new Color(0x00000000));
			graphMask.fillRect(0, 0, maskImage.getWidth(), maskImage.getHeight());
			graphMask.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			graphMask.setColor(Color.white);
			graphMask.fillPolygon(poly);
			
			// SOURCE IMAGE
			Graphics2D graphSrc = (Graphics2D) srcImage.getGraphics();
			// Copy mask as the background for srcImage
			graphSrc.drawImage(maskImage, 0,0, null);
			// Paste over original image using the correct AlphaComposite
			graphSrc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
			graphSrc.drawImage(origImage,0,0,srcImage.getWidth(),srcImage.getHeight(),topleft.x,topleft.y,srcImage.getWidth()+topleft.x,srcImage.getHeight()+topleft.y,null);
			
			
			try {
				//TODO get rid of this
				ImageIO.write(srcImage, "PNG", new File("C://mask1.png"));
	
			} catch (IOException e1) {
	
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				selection.setMode(false);
				System.out.println("selectionMode setted to false");
			}
		} else {
			Component c = e.getComponent();
			if (!(e.getPoint().x < 0 || e.getPoint().y < 0  || e.getPoint().x > c.getWidth() || e.getPoint().y > c.getHeight())) {
				// if selection mode == false and the mouse was released in the image, clear selection
				points.clear();
				
				//TODO Frank thinks this is code smell, find a better way to do it
				c.repaint();
				
				// let's go again in selectionMode 
				selection.setMode(true); 
			}
			
			// Ghostly drag and drop
	        Point p = (Point) e.getPoint().clone();
	        SwingUtilities.convertPointToScreen(p, c);

	        Point eventPoint = (Point) p.clone();
	        SwingUtilities.convertPointFromScreen(p, glassPane);

	        glassPane.setPoint(p);
	        glassPane.setVisible(false);
	        glassPane.setImage(null);

	        fireGhostDropEvent(new GhostDropEvent(action, eventPoint));
		}
	}
}