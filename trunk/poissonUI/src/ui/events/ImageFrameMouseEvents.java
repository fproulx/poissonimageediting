package ui.events;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class ImageFrameMouseEvents implements MouseListener,

MouseMotionListener {

	// private Stack<Point> selection = new Stack<Point>();

	private ArrayList<Point> selection = new ArrayList<Point>();
	
	// state for selection (selectionMode or dragMode)
	private boolean selectionMode = true;

	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse clicked (# of clicks: " + e.getClickCount()
		+ ")");
	}

	public void mouseEntered(MouseEvent e) {
		System.out.println("Mouse entered");
	}

	public void mouseExited(MouseEvent e) {
		System.out.println("Mouse exited");
	}

	public void mousePressed(MouseEvent e) {

		// We are in selection mode
		if (selectionMode == true) {
			selection.clear();
			System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
			selection.add(e.getPoint());
			Graphics g = e.getComponent().getGraphics();
			g.drawRect(e.getPoint().x, e.getPoint().y, 1, 1);
			
			System.out.println("Pressed" + e.getPoint());
	
			// TODO
			// if(!selection.isEmpty()) {
	
			/*
			 * JComponent c = (JComponent)e.getSource();
			 * TransferHandler handler = c.getTransferHandler();
			 * handler.exportAsDrag(c, e, TransferHandler.COPY);
			 */
	
			// }
		}
	}

	public void mouseReleased(MouseEvent e) {

		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
		System.out.println("State selection mode: "+selectionMode);

		if (selectionMode == true) {
		
			// Trouver le premier point cliqué et le dernier
			int indexDernierPoint = selection.size() - 1;
			Point premierPoint = selection.get(0);
			Point dernierPoint = selection.get(indexDernierPoint);
			Graphics g = e.getComponent().getGraphics();
	
			// ajouter les points de la ligne entre le premier point et le dernier
			if (premierPoint.x > dernierPoint.x) {
				premierPoint = dernierPoint;
				dernierPoint = selection.get(0);
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
	
				if (selection.contains(pointLigne))
					break;
				else
					selection.add(pointLigne);
	
				System.out.println(pointLigne);
				g.drawLine(x0, y0, x1, y1);
			}
	
			int maxX = 0;
			int minX = selection.get(0).x;
			int maxY = 0;
			int minY = selection.get(0).y;
	
			// Trouver le min en x
			for (int i = 0; i < selection.size(); i++) {
				if (selection.get(i).x > maxX) {
					maxX = selection.get(i).x;
				}
	
				if (selection.get(i).x < minX) {
					minX = selection.get(i).x;
				}
	
				if (selection.get(i).y > maxY) {
					maxY = selection.get(i).y;
				}
	
				if (selection.get(i).y < minY) {
					minY = selection.get(i).y;
				}
			}
	
			BufferedImage mask = new BufferedImage(maxX - minX, maxY - minY,
					BufferedImage.TYPE_INT_RGB);
	
			// int[][] mask = new int[maxX-minX][maxY-minY];
			for (int i = minX; i < maxX; i++) {
				boolean flip = false;
	
				for (int j = minY; j < maxY; j++) {
	
					if (selection.contains(new Point(i, j))
						&& !selection.contains(new Point(i, j - 1))
						&& !selection.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (selection.contains(new Point(i, j))
						&& !selection.contains(new Point(i, j - 1))
						&& selection.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (selection.contains(new Point(i, j))
						&& selection.contains(new Point(i, j - 1))
						&& !selection.contains(new Point(i, j + 1))) {
							flip = true;
					}
	
					if (selection.contains(new Point(i, j))
						&& selection.contains(new Point(i, j - 1))
						&& selection.contains(new Point(i, j + 1))) {
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
				selectionMode = false;
			}
		} else {
			JComponent c = (JComponent)e.getSource();
			if (e.getPoint().x < 0 || e.getPoint().y < 0 
					|| e.getPoint().x > c.getWidth() || e.getPoint().y > c.getHeight()) {
				
				// TODO drag and drop code
				
			} else {
				// if selection mode == false and the mouse was released in the image, clear selection
				selection.clear();
				
				//TODO Frank thinks this is code smell, find a better way to do it
				c.repaint();
				
				// let's go again in selectionMode
				selectionMode = true;
			}
		}
	}

	public void mouseDragged(MouseEvent e) {

		if (selectionMode == true) {
			if (!selection.contains(e.getPoint())) {
	
				System.out.println("mouseDragged at " + e.getPoint());
				selection.add(e.getPoint());
				Graphics g = e.getComponent().getGraphics();
				g.drawRect(e.getPoint().x, e.getPoint().y, 1, 1);
	
			}
		}
	}

	public void mouseMoved(MouseEvent e) {

	}

}
