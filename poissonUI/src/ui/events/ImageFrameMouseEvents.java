package ui.events;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * Classe ImageFrameMouseEvents implémente les actions de la souris pour la
 * sélection
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 * 
 */
public class ImageFrameMouseEvents implements MouseListener,
		MouseMotionListener {

	private ArrayList<Point> selection = new ArrayList<Point>();

	private boolean pressed = false;

	public void mouseClicked(MouseEvent e) {
		clean();
	}

	public void mouseEntered(MouseEvent e) {
		// System.out.println("Mouse entered");
	}

	public void mouseExited(MouseEvent e) {
		// System.out.println("Mouse exited");
	}

	public void mousePressed(MouseEvent e) {
		// efface la derniere sélection et commence le processus de selection
		clean();
		selection.add(e.getPoint());
		pressed = true;

		// dessine de quoi sur la sélection
		draw(e.getComponent().getGraphics(), e.getPoint().x, e.getPoint().y);

		// TODO
		// if(!selection.isEmpty()) {
		/*
		 * JComponent c = (JComponent)e.getSource(); TransferHandler handler =
		 * c.getTransferHandler();
		 * 
		 * handler.exportAsDrag(c, e, TransferHandler.COPY);
		 */
		// }
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released; # of clicks: " + e.getClickCount());
		pressed = false;

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

		// algorithme ligne be*...
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
			if (!selection.contains(pointLigne)) {
				selection.add(pointLigne);
			}
			draw(g, x, y);
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (!selection.contains(e.getPoint()) && pressed) {
			selection.add(e.getPoint());
			draw(e.getComponent().getGraphics(), e.getPoint().x, e.getPoint().y);
		} else {
			pressed = false;
		}
	}

	public void mouseMoved(MouseEvent arg0) {

	}

	/**
	 * Dessine la selection
	 * 
	 * @param e
	 *            est le event
	 * @param x
	 *            est la position en x
	 * @param y
	 *            est la position en y
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawRect(x, y, 1, 1);
	}

	/**
	 * 
	 * @return selection
	 */
	public ArrayList<Point> getSelection() {
		return selection;
	}

	/**
	 * TODO clean selection
	 */
	private void clean() {
		selection.clear();
	}
}