package ca.etsmtl.photomontage.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * @author fproulx
 *
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 6195069328645116258L;
	private BufferedImage img;
 
    /**
     * @param img
     */
    public ImagePanel(BufferedImage img) {
    	setImage(img);
    }
 
    public BufferedImage getImage() {
		return img;
	}

	public void setImage(BufferedImage img) {
		if(this.img != null) {
			synchronized(this.img) {
				this.img = img;
			}
		}
		else {
			this.img = img;
		}
		
		setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
		repaint();
	}	

	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    }
 
}