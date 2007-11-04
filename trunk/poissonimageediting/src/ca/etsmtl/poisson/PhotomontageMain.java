package ca.etsmtl.poisson;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author fproulx
 *
 */
public class PhotomontageMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Paths to all the images
		String srcImagePath = "";
		String maskImagePath = "";
		String destImagePath = "";
		
		try {
			// Load all the images
			BufferedImage srcImage = ImageIO.read(new File(srcImagePath));
			BufferedImage maskImage = ImageIO.read(new File(maskImagePath));
			BufferedImage destImage = ImageIO.read(new File(destImagePath));

			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(10, 10));
			// Do the heavy lifting
			BufferedImage output = photomontage.createPhotomontage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
