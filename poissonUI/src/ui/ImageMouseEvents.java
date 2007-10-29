package ui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Stack;


public class ImageMouseEvents implements MouseListener, MouseMotionListener {

	private Stack<Point> selection = new Stack<Point>();
	
	private boolean mousePressed = false;
	
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse clicked (# of clicks: " + e.getClickCount() + ")");		
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
		mousePressed = true;
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
		mousePressed = false;
				
		
	}

	public void mouseDragged(MouseEvent e) {
		if(!selection.contains(e.getPoint())){
			selection.add(e.getPoint());
		}
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
