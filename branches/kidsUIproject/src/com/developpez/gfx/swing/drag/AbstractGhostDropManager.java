package com.developpez.gfx.swing.drag;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Handling events related to droping portion of the translucent drag and drop. 
 */
public abstract class AbstractGhostDropManager implements GhostDropListener {
	protected JComponent component;

	/**
	 * Constructor calling specific constructor using null component
	 */
	public AbstractGhostDropManager() {
		this(null);
	}
	
	/**
	 * Constructor
	 * @param component The component on which components will be dropped.
	 */
	public AbstractGhostDropManager(JComponent component) {
		this.component = component;
	}

	protected Point getTranslatedPoint(Point point) {
        Point p = (Point) point.clone();
        SwingUtilities.convertPointFromScreen(p, component);
		return p;
	}

	protected boolean isInTarget(Point point) {
		Rectangle bounds = component.getBounds();
		return bounds.contains(point);
	}

	public void ghostDropped(GhostDropEvent e) {
	}
}