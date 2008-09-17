package com.developpez.gfx.swing.drag;

/**
 * Event listener interface for the transparent drop events
 */
public interface GhostDropListener {
	
	/**
	 * Reimplement this method with what you want to be done on a drop event 
	 * @param e Event
	 */
	public void ghostDropped(GhostDropEvent e);
}
