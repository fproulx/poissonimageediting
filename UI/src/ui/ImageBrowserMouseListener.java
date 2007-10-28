/*
 * ImageBrowserMouseListener.java
 *
 * Created on Oct 15, 2007, 9:21:34 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author jeanphilippe
 */
public class ImageBrowserMouseListener implements MouseListener {

    private ImageHolder image;
    private ImageFramesContainer container;
    private PreviewContainer preview;

    public ImageBrowserMouseListener(ImageHolder image, ImageFramesContainer container, PreviewContainer preview) {
        this.image = image;
        this.container = container;
        this.preview = preview;
    }

    public void mouseClicked(MouseEvent arg0) {
        ImageFrame img = new ImageFrame("Untitled", image, container, preview);
        container.add(img);
    }

    public void mousePressed(MouseEvent arg0) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent arg0) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent arg0) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}