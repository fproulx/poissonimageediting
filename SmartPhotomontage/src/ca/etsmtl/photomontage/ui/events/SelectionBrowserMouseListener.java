package ca.etsmtl.photomontage.ui.events;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.exceptions.InvalidDestinationPositionException;
import ca.etsmtl.photomontage.exceptions.InvalidMaskException;
import ca.etsmtl.photomontage.exceptions.InvalidSourceImageSizeException;
import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;
import ca.etsmtl.photomontage.ui.ImageFrame;
import ca.etsmtl.photomontage.ui.SelectionBrowser;
import ca.etsmtl.photomontage.ui.UIApp;
import ca.etsmtl.photomontage.ui.UIView;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;

import com.developpez.gfx.swing.drag.GhostDropAdapter;
import com.developpez.gfx.swing.drag.GhostGlassPane;

//TODO: Fix bug disappearing lines ?!
/**
 *
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 * <lebel.kim@gmail.com>, Jean-Philippe Plante
 * <jphilippeplante@gmail.com>, Francois Proulx
 * <francois.proulx@gmail.com>
 * 
 */

public class SelectionBrowserMouseListener extends GhostDropAdapter {
	
	// Image that represents the selected part of the image
	private final SelectionHolder selectionHolder;

	/**
	 * @param glassPane
	 * @param holder
	 */
	public SelectionBrowserMouseListener(final GhostGlassPane glassPane, final SelectionHolder holder) {
		super(glassPane, null);
		
		this.selectionHolder = holder;
	}

	public void mousePressed(MouseEvent e) {

		Component c = e.getComponent();

        glassPane.setVisible(true);

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setImage(selectionHolder.getImage());
        glassPane.repaint();
	}

	public void mouseReleased(MouseEvent e) {

		Component c = e.getComponent();

		// Ghostly drag and drop
        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);

        Point eventPoint = (Point) p.clone();
        SwingUtilities.convertPointFromScreen(p, glassPane);

        glassPane.setPoint(p);
        glassPane.setVisible(false);
        glassPane.setImage(null);

        // WARNING !!!! This is a big stinky HACK
        
        // Get ahold of the source and destination components for the drag-drop event
        UIView appView = (UIView) UIApp.getApplication().getMainView();
        JDesktopPane desktop = appView.getImageFramesDesktop();
        SelectionBrowser selectionBrowser = appView.getSelectionBrowser();
        
        // Convert the point relative to the Desktop area
        Point desktopConvertedPoint = SwingUtilities.convertPoint(selectionBrowser, e.getPoint(), desktop);
        // find the component that under this point
        Component component = SwingUtilities.getDeepestComponentAt(desktop, desktopConvertedPoint.x, desktopConvertedPoint.y);
        //TODO: this should check if it is actually the right JLabel (inside an ImageFrame)
        if(component instanceof JLabel) {
        	JLabel dstComponent = (JLabel) component;
        	
        	// convert point relative to the target component
        	final Point dstPoint = SwingUtilities.convertPoint(selectionBrowser, e.getPoint(), dstComponent);
        	final ImageFrame frame = (ImageFrame) dstComponent.getParent().getParent().getParent().getParent().getParent();

        	new Thread() {
        		public void run() {
        			// Setup the Poisson solver
        			PoissonPhotomontage photomontage = new PoissonPhotomontage(selectionHolder.getImage(), selectionHolder.getMaskImage(), frame.getImageHolder().getImage(), dstPoint);
        			
        			BufferedImage output = null;
        			try {
	        			// Do the heavy lifting
	        			long t0 = System.nanoTime();
	        			output = photomontage.createPhotomontage();
	        			long t1 = System.nanoTime();
	        			
	        			// 2573864000 ns --> 2.573864 s
	        			System.out.printf("%d ns --> %f s\r\n", t1 - t0, (t1 - t0) / Math.pow(10, 9));
	        			
	        			// Replace the image with the photomontage
	        			ImageHolder newImageHolder = new ImageHolder(output, frame.getImageHolder().getFilename());
	        			frame.setImageHolder(newImageHolder);
	        			
        			} catch (ComputationException e) {
						e.printStackTrace();
					} catch (InvalidSourceImageSizeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidDestinationPositionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvalidMaskException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        	}.start();
        }
   
        //TODO fix this!
        //fireGhostDropEvent(new SelectionGhostDropEvent(srcImage, maskImage, eventPoint));
	}
}