package ca.etsmtl.poisson;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

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
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(10, 10));
			// Do the heavy lifting
			BufferedImage output = photomontage.createPhotomontage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
	}

}
