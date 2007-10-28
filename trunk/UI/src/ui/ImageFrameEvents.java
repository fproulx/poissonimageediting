/*
 * ImageFrameEvent.java
 *
 * Created on Oct 28, 2007, 12:59:46 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author jeanphilippe
 */
public class ImageFrameEvents implements InternalFrameListener {

    private ImageFrame frame;

    public ImageFrameEvents(ImageFrame frame) {
        this.frame = frame;
    }

    public void internalFrameOpened(InternalFrameEvent arg0) {
        //nothing...
    }

    public void internalFrameClosing(InternalFrameEvent arg0) {
        //TODO ask save
    }

    public void internalFrameClosed(InternalFrameEvent arg0) {
        frame.close();
    }

    public void internalFrameIconified(InternalFrameEvent arg0) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void internalFrameDeiconified(InternalFrameEvent arg0) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void internalFrameActivated(InternalFrameEvent arg0) {
        //nothing...
    }

    public void internalFrameDeactivated(InternalFrameEvent arg0) {
        //nothing...
    }
}