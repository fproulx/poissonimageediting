/*
 * ImageHolder.java
 * 
 * Created on Oct 28, 2007, 11:57:46 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Calendar;

/**
 * This is a utility class that holds our images at various scaled
 * sizes. The images are pre-scaled down by halves, using the progressive
 * bilinear technique. Thumbnails from these images are requested
 * from this class, which are created by down-scaling from the next-largest
 * pre-scaled size available.
 */
public class ImageHolder {
    private BufferedImage scaledImage;
    
    private BufferedImage original;
    
    /**
     * Given any image, this constructor creates and stores down-scaled
     * versions of this image down to some MIN_SIZE
     */
    public ImageHolder(BufferedImage originalImage) {
        this.original = originalImage;
        this.scaledImage = getImage(ImageBrowser.currentSize);
    }

    public BufferedImage getScaledImage() {
        return scaledImage;
    }
    
    public BufferedImage getOriginal() {
        return original;
    }

    /**
     * This method returns an image with the specified width. It finds
     * the pre-scaled size with the closest/larger width and scales
     * down from it, to provide a fast and high-quality scaed version
     * at the requested size.
     */
    BufferedImage getImage(int width) {
        float scaleFactor = (float) width / original.getWidth();
        int scaledH = (int) (original.getHeight() * scaleFactor);

        BufferedImage img = new BufferedImage(width, scaledH, original.getType());
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(original, 0, 0, width, scaledH, null);
        g2d.dispose();     
       
        return img;
    }
}
