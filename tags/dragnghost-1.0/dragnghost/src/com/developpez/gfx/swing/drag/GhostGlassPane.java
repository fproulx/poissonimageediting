package com.developpez.gfx.swing.drag;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A semi-transparent pane that is placed in front. 
 * The semi-transparent drag and drop effect is painted on this pane.
 */
public class GhostGlassPane extends JPanel
{
	private static final long serialVersionUID = -779556448336535275L;
	private AlphaComposite composite;
    private BufferedImage dragged = null;
    private Point location = new Point(0, 0);

    /**
     * Create the semi-transparent pane 
     */
    public GhostGlassPane()
    {
        setOpaque(false);
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    }

    /**
     * Set the image that is dragged around
     * @param dragged image
     */
    public void setImage(BufferedImage dragged)
    {
        this.dragged = dragged;
    }

    /**
     * Set the point that will be the center of where we paint the image 
     * @param location
     */
    public void setPoint(Point location)
    {
        this.location = location;
    }

    /**
     * Paint the image on the pane using composite to make it semi-transparent
     */
    public void paintComponent(Graphics g)
    {
        if (dragged == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(composite);
        g2.drawImage(dragged,
                     (int) (location.getX() - (dragged.getWidth(this)  / 2)),
                     (int) (location.getY() - (dragged.getHeight(this) / 2)),
                     null);
    }
}