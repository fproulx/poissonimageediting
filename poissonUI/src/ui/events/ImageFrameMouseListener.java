package ui.events;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
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
	
	// Image that represents the selected part of the image (used for drag and drop effect)
	private BufferedImage ghostImage;
	
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
	        glassPane.setImage(origImage);
	        glassPane.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {

		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
		System.out.println("State selection mode: "+selection.isSelectionMode());
		List<Point> points = selection.getPoints();
		
		if (selection.isSelectionMode() == true) {	
			// Trouver le premier point cliqué et le dernier
			int indexDernierPoint = points.size() - 1;
			Point premierPoint = points.get(0);
			Point dernierPoint = points.get(indexDernierPoint);
			Graphics g = e.getComponent().getGraphics();
	
			// ajouter les points de la ligne entre le premier point et le dernier
			if (premierPoint.x > dernierPoint.x) {
				premierPoint = dernierPoint;
				dernierPoint = points.get(0);
			}
	
			int x0 = premierPoint.x;
			int y0 = premierPoint.y;
			int x1 = dernierPoint.x - 1;
			int y1 = dernierPoint.y - 1;
			int dx = x1 - x0;
			int dy = y1 - y0;
			int x = x0;
			int y = y0;
			int d = -dx + 2 * dy;
			int dE = 2 * dy;
			int dNE = 2 * dy - 2 * dx;
	
			while (x <= x1) {
				if (d <= 0) {
					d += dE;
				} else {
					d += dNE;
					++y;
				}
	
				++x;
	
				Point pointLigne = new Point(x, y);
	
				if (points.contains(pointLigne))
					break;
				else
					points.add(pointLigne);
	
				System.out.println(pointLigne);
				g.drawLine(x0, y0, x1, y1);
			}
	
			int maxX = 0;
			int minX = points.get(0).x;
			int maxY = 0;
			int minY = points.get(0).y;
	
			// Trouver le min en x
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).x > maxX) {
					maxX = points.get(i).x;
				}
	
				if (points.get(i).x < minX) {
					minX = points.get(i).x;
				}
	
				if (points.get(i).y > maxY) {
					maxY = points.get(i).y;
				}
	
				if (points.get(i).y < minY) {
					minY = points.get(i).y;
				}
			}
	
			BufferedImage mask = new BufferedImage(maxX - minX, maxY - minY,
					BufferedImage.TYPE_INT_RGB);
	
			// int[][] mask = new int[maxX-minX][maxY-minY];
			for (int i = minX; i < maxX; i++) {
				boolean flip = false;
	
				for (int j = minY; j < maxY; j++) {
	
					if (points.contains(new Point(i, j))
						&& !points.contains(new Point(i, j - 1))
						&& !points.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (points.contains(new Point(i, j))
						&& !points.contains(new Point(i, j - 1))
						&& points.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (points.contains(new Point(i, j))
						&& points.contains(new Point(i, j - 1))
						&& !points.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (points.contains(new Point(i, j))
						&& points.contains(new Point(i, j - 1))
						&& points.contains(new Point(i, j + 1))) {
							flip = false;
					}
	
					if (flip) {
						// mask[i - minX][j - minY] = 1;
						mask.setRGB(i - minX, j - minY, 0xFFFFFFFF);
	
					} else {
	
						// mask[i - minX][j - minY] = 0;
						mask.setRGB(i - minX, j - minY, 0);
					}
				}
			}
	
			/*
			 * for(int i = 0; i < mask.length;i++) {
			 * 		for(int j = 0; j < mask[i].length;j++) {
			 * 			System.out.print(mask[i][j]);
			 *  	}
			 * 		System.out.println();
			 * }
			 */
	
			try {
				ImageIO.write(mask, "PNG", new File("C://mask1.png"));
	
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