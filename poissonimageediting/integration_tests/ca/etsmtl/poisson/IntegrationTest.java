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
	public void notifyComputationListeners(T s, T m, T o);
}

public class IntegrationTest implements DataComputer<BufferedImage> {
	BufferedImage srcImage, maskImage, destImage;
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
	
	public void notifyComputationListeners(BufferedImage src, BufferedImage mask, BufferedImage out) {
		if(computationListeners != null) {
			for(ComputationListener<BufferedImage> cl: computationListeners) {
				cl.onComputationCompleted(src, mask, output);
			}
		}	
	}
	
	public BufferedImage computeData() {
		try {
			/*
			// Load all the images
			srcImage = ImageIO.read(new File("resources/images/objects/duck.jpg"));
			maskImage = ImageIO.read(new File("resources/images/masks/duck.png"));
			destImage = ImageIO.read(new File("resources/images/backgrounds/green_lake_with_duck.png"));
			 
			srcImage = ImageIO.read(new File("resources/images/tests/validateInput/src-small.png"));
			maskImage = ImageIO.read(new File("resources/images/tests/validateInput/mask-best.png"));
			destImage = ImageIO.read(new File("resources/images/tests/validateInput/dst.png"));
			*/
			srcImage = ImageIO.read(new File("resources/images/objects/F16.png"));
			maskImage = ImageIO.read(new File("resources/images/masks/F16mask.png"));
			destImage = ImageIO.read(new File("resources/images/backgrounds/F16Target.jpg"));
			
			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(215, 150));
			
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
			notifyComputationListeners(srcImage, maskImage, output);
		}
		
		return null;
	}
}

interface ComputationListener<T> {
	public void onComputationCompleted(T t1, T t2, T t3);
}

class ComputationResultsDisplay extends JFrame implements ComputationListener<BufferedImage> {
	private static final long serialVersionUID = -602143731782699959L;
	private JPanel displayPanel = new JPanel();
	private volatile BufferedImage s, m, computedImage;
	
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
						g.drawImage(s, 0, 0, null);
						g.drawImage(m, 0, s.getHeight(), null);
						g.drawImage(computedImage, s.getWidth(), 0, null);
					}
				}
			}
		};
		add(displayPanel);
	}

	public void onComputationCompleted(BufferedImage s, BufferedImage m, BufferedImage o) {
		if(computedImage != null) {
			synchronized(computedImage) {
				computedImage = (BufferedImage) o;
			}
		}
		else {
			computedImage = (BufferedImage) o;
		}
		this.s = s;
		this.m = m;
		repaint();
	}
}
