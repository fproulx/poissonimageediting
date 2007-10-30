package ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Stack;

/**
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 * 
 */
public class ImageFrameMouseEvents implements MouseListener,
		MouseMotionListener {

	private Stack<Point> selection = new Stack<Point>();

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
		System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
		selection.add(e.getPoint());
		System.out.println("Pressed" + e.getPoint());
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
	}

	public void mouseDragged(MouseEvent e) {
		if (!selection.contains(e.getPoint())) {
			selection.add(e.getPoint());
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
