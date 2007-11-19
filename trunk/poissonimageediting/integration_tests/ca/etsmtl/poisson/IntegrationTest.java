package ca.etsmtl.poisson;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;

/**
 * 
 * @author fproulx
 *
 */
interface DataComputer<T> {
	public T computeData();
	public void addComputationListener(ComputationListener<T> cl);
	public void notifyComputationListeners(T o);
}

public class IntegrationTest implements DataComputer<BufferedImage> {
	BufferedImage output;
	List<ComputationListener<BufferedImage>> computationListeners = new ArrayList<ComputationListener<BufferedImage>>();
	
	public static void main(String[] args) {
		IntegrationTest test = new IntegrationTest();
		test.computeData();
	}
	
	public IntegrationTest() {
		final DataComputer<BufferedImage> self = this;
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	ComputationResultsDisplay frame = new ComputationResultsDisplay(self);
            	addComputationListener(frame);
                frame.setVisible(true);
            }
        });
	}
	
	public void addComputationListener(ComputationListener<BufferedImage> listener) {
		computationListeners.add(listener);
	}
	
	public void notifyComputationListeners(BufferedImage o) {
		if(computationListeners != null) {
			for(ComputationListener<BufferedImage> cl: computationListeners) {
				cl.onComputationCompleted(output);
			}
		}	
	}
	
	public BufferedImage computeData() {
		// top-level path
		String testImgPath = "resources/images/tests/";

		// file references
		String dstImagePath = "validateInput/dst.png";
		String srcSmallImagePath = "validateInput/src-small.png";
		String maskValidImagePath = "validateInput/mask-valid.png";	
		
		try {
			// Load all the images
			BufferedImage srcImage = ImageIO.read(new File(testImgPath + srcSmallImagePath));
			BufferedImage maskImage = ImageIO.read(new File(testImgPath + maskValidImagePath));
			BufferedImage destImage = ImageIO.read(new File(testImgPath + dstImagePath));

			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(95, 95));
			
			// Do the heavy lifting
			long t0 = System.nanoTime();
			output = photomontage.createPhotomontage();
			long t1 = System.nanoTime();
			
			// 2573864000 ns --> 2.573864 s
			System.out.printf("%d ns --> %f s\r\n", t1 - t0, (t1 - t0) / Math.pow(10, 9));
			
			return output;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			notifyComputationListeners(output);
		}
		
		return null;
	}

}

interface ComputationListener<T> {
	public void onComputationCompleted(T o);
}

class ComputationResultsDisplay extends JFrame implements ComputationListener<BufferedImage> {
	private static final long serialVersionUID = -602143731782699959L;
	private JPanel displayPanel = new JPanel();
	private volatile BufferedImage computedImage;
	
	public ComputationResultsDisplay(DataComputer<BufferedImage> dc) {
		dc.addComputationListener(this);
		initComponents();
	}
	
	public void initComponents() {
		setSize(1024, 768);
		setLocationRelativeTo(null);
		
		displayPanel = new JPanel() {
			public void paint(Graphics g) {
				if(computedImage != null) {
					synchronized(computedImage) {
						g.drawImage(computedImage, 0, 0, null);
					}
				}
			}
		};
		add(displayPanel);
	}

	public void onComputationCompleted(BufferedImage o) {
		if(computedImage != null) {
			synchronized(computedImage) {
				computedImage = (BufferedImage) o;
			}
		}
		else {
			computedImage = (BufferedImage) o;
		}
		repaint();
	}
}
