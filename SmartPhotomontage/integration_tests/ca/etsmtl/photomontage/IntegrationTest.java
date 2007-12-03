/*
 * SmartPhotomontage
 * Copyright (C) 2007
 * François Proulx, Olivier Bilodeau, Jean-Philippe Plante, Kim Lebel
 * http://poissonimageediting.googlecode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */

package ca.etsmtl.photomontage;

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
	/**
	 * 
	 * @return T
	 */
	public T computeData();
	
	/**
	 * Add Computation listener
	 * 
	 * @param cl
	 */
	public void addComputationListener(ComputationListener<T> cl);
	
	/**
	 * Notify all listeners
	 * 
	 * @param s
	 * @param m
	 * @param o
	 */
	public void notifyComputationListeners(T s, T m, T o);
}

/**
 * IntegrationTest for integration testing with a UI
 *
 */
public class IntegrationTest implements DataComputer<BufferedImage> {
	BufferedImage srcImage, maskImage, destImage;
	BufferedImage output;
	List<ComputationListener<BufferedImage>> computationListeners = new ArrayList<ComputationListener<BufferedImage>>();
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		IntegrationTest test = new IntegrationTest();
		test.computeData();
	}
	
	/**
	 * Constructor for integration testing
	 */
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
			// Load all the images
//			srcImage = ImageIO.read(new File("resources/images/objects/duck.jpg"));
//			maskImage = ImageIO.read(new File("resources/images/masks/duck.png"));
//			destImage = ImageIO.read(new File("resources/images/backgrounds/green_lake_with_duck.png"));
//			Point dstPoint = new Point(95, 95);
			
			// Goat
			srcImage = ImageIO.read(new File("resources/images/tests/validateInput/src-small.png"));
			maskImage = ImageIO.read(new File("resources/images/tests/validateInput/mask-best2.png"));
			destImage = ImageIO.read(new File("resources/images/tests/validateInput/dst.png"));
			Point dstPoint = new Point(95, 95);
			
			// Diver
//			srcImage = ImageIO.read(new File("resources/images/presentation/diver-fg.jpg"));
//			maskImage = ImageIO.read(new File("resources/images/presentation/diver-mask.png"));
//			destImage = ImageIO.read(new File("resources/images/presentation/diver-bg.jpg"));
//			Point dstPoint = new Point(220, 5);
			
			// Ovni
//			srcImage = ImageIO.read(new File("resources/images/presentation/ovni-fg.jpg"));
//			maskImage = ImageIO.read(new File("resources/images/presentation/ovni-mask.png"));
//			destImage = ImageIO.read(new File("resources/images/presentation/ovni-bg.jpg"));
//			Point dstPoint = new Point(120, 2);
			
			// Parachute
//			srcImage = ImageIO.read(new File("resources/images/presentation/parachute-fg.jpg"));
//			maskImage = ImageIO.read(new File("resources/images/presentation/parachute-mask.png"));
//			destImage = ImageIO.read(new File("resources/images/presentation/parachute-bg.jpg"));
//			Point dstPoint = new Point(50, 45);

			
			/*srcImage = ImageIO.read(new File(chooser.getSelectedFile().getName()));
			maskImage = ImageIO.read(new File("resources/images/masks/F16mask.png"));
			destImage = ImageIO.read(new File("resources/images/backgrounds/F16Target.jpg"));*/
			
			/*
			JFileChooser chooser = new JFileChooser();
		    
			chooser.setCurrentDirectory(new File("resources/images/"));
			chooser.showOpenDialog(null);
		    srcImage = ImageIO.read(chooser.getSelectedFile());
		    
		    chooser.setCurrentDirectory(new File("resources/images/"));
		    chooser.showOpenDialog(null);
		    maskImage = ImageIO.read(chooser.getSelectedFile());
		    
		    chooser.setCurrentDirectory(new File("resources/images/"));
		    chooser.showOpenDialog(null);
		    destImage = ImageIO.read(chooser.getSelectedFile());
		    
		    int x, y;
		    x = Integer.parseInt(JOptionPane.showInputDialog("X"));
		    y = Integer.parseInt(JOptionPane.showInputDialog("Y"));
		    
		    */
			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, dstPoint);
			
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
	/**
	 * when computation completed
	 * @param t1
	 * @param t2
	 * @param t3
	 */
	public void onComputationCompleted(T t1, T t2, T t3);
}

class ComputationResultsDisplay extends JFrame implements ComputationListener<BufferedImage> {
	private static final long serialVersionUID = -602143731782699959L;
	private JPanel displayPanel = new JPanel();
	private volatile BufferedImage s, m, computedImage;
	
	/**
	 * Constructor
	 * @param dc
	 */
	public ComputationResultsDisplay(DataComputer<BufferedImage> dc) {
		dc.addComputationListener(this);
		initComponents();
	}
	
	/**
	 * Initialize components
	 */
	public void initComponents() {
		setSize(1024, 768);
		setLocationRelativeTo(null);
		
		displayPanel = new JPanel() {
			/**
			 * generated UID
			 */
			private static final long serialVersionUID = 7178866588266350768L;

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
