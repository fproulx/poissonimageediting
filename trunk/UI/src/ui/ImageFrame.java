/*
 * ImageFrame.java
 *
 * Created on 2007-10-21, 15:29:35
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author ag95300
 */
public class ImageFrame extends JInternalFrame {

    private BufferedImage image;
    
    private boolean modified = false;
    
    private ImagesContainer container;

    public ImageFrame(String title, BufferedImage img,ImagesContainer container) {
        super(title, true, true, true);

        this.image = img;
        this.container = container;

        JPanel panel = new JPanel();
        JLabel label = new JLabel();

        label.setIcon(new ImageIcon(image));

        panel.add(label);
        add(panel);

        //TODO add icon
        
        setSize(img.getWidth(), img.getHeight());
        setVisible(true);
        setAutoscrolls(true);
        setFocusable(true);
        
        requestFocus();
        
        addInternalFrameListener(new ImageFrameEvents(this));
        
        System.out.println("ImageFrame.ImageFrame " + img.getWidth() + "x" + img.getHeight());
    }

    public boolean isModified() {
        return modified;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void close() {
        container.remove(this);
    }
}