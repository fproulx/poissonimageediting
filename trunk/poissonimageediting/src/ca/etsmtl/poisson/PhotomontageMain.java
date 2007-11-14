package ca.etsmtl.poisson;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * 
 * @author fproulx
 *
 */
public class PhotomontageMain extends JFrame {

	BufferedImage srcImage;
	BufferedImage maskImage;
	BufferedImage destImage;
	BufferedImage output;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PhotomontageMain f = new PhotomontageMain();
		f.compute();
	}
	
	public PhotomontageMain() {
		setSize(1024, 768);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void compute() {
		// top-level path
		String testImgPath = "resources/images/tests/";

		// file references
		String dstImagePath = "validateInput/dst.png";
		String srcSmallImagePath = "validateInput/src-small.png";
		String maskValidImagePath = "validateInput/mask-valid.png";	
		
		try {
			// Load all the images
			srcImage = ImageIO.read(new File(testImgPath + srcSmallImagePath));
			maskImage = ImageIO.read(new File(testImgPath + maskValidImagePath));
			destImage = ImageIO.read(new File(testImgPath + dstImagePath));

			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(10, 10));
			// Do the heavy lifting
			output = photomontage.createPhotomontage();
			repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g) {
		if(output != null) {
			g.drawImage(srcImage, 0, 0, null);
			g.drawImage(maskImage, 0, srcImage.getHeight(), null);
			g.drawImage(destImage, 0, destImage.getHeight() + srcImage.getHeight(), null);
			g.drawImage(output, destImage.getWidth(), 0, null);
		}
	}

}
