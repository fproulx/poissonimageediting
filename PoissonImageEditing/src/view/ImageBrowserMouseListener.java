/*
 * ImageBrowserMouseListener.java
 * 
 * Created on Oct 15, 2007, 9:21:34 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author jeanphilippe
 */
public class ImageBrowserMouseListener implements MouseListener {

    private int i;

    public ImageBrowserMouseListener(int i) {
        this.i = i;
        
    }

    public void mouseClicked(MouseEvent arg0) {
        System.out.println("Image " + i + " mouseClicked");
        
       // JInternalFrame frame = new JInternalFrame();
        
       // JLabel label = new JLabel();
       // label.setIcon(new ImageIcon(ImageBrowser.images.get(i).getImage(250)));
        
       // setChanged();
       // notifyObservers(frame);
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
