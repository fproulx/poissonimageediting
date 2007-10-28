/*
 * ImagesContainer.java
 * 
 * Created on 2007-10-22, 11:09:57
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author ag95300
 */
public class ImagesContainer extends Observable {
    
    private List<ImageFrame> imageFrames = new ArrayList<ImageFrame>();
    
    public void add(ImageFrame img) {
        System.out.println("ImagesContainer.add");
        
        //add it and notify the observers
        if(!contains(img) && img != null) {
            imageFrames.add(img);
            setChanged();
            notifyObservers(imageFrames);
        }
    }
   
    public void remove(ImageFrame frame) {
        if(imageFrames.contains(frame) && frame != null) {
            imageFrames.remove(frame);
            setChanged();
            notifyObservers(imageFrames);
        }
    }
    
    public boolean contains(ImageFrame frame) {
        //check if its in the imageframe list
        for(ImageFrame myframe: imageFrames) {
            if(frame.getImage().hashCode() == myframe.getImage().hashCode()) {
                return true;
            }
        }
        return false;
    }
    
    public List<ImageFrame> getFrames() {
        return imageFrames;
    }
}
