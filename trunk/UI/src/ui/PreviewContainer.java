/*
 * PreviewContainer.java
 * 
 * Created on Oct 28, 2007, 1:46:07 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.image.BufferedImage;
import java.util.Observable;

/**
 *
 * @author jeanphilippe
 */
public class PreviewContainer extends Observable {
    
    private BufferedImage scaledImage;
    
    public PreviewContainer() {
        
    }
 
    public BufferedImage getScaledImage() {
        return scaledImage;
    }
    
    public void update(BufferedImage scaledImage) {
        System.out.println("PreviewContainer.update");
        
        this.scaledImage = scaledImage;
        setChanged();
        notifyObservers();
    }
}
