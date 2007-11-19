package ca.etsmtl.poisson;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;

public class BatchTesting {
	public static void main(String args[]) throws IOException, ComputationException {
		// top-level path
		String testImgPath = "resources/images/tests/";

		// file references
		String dstImagePath = "validateInput/dst.png";
		String srcSmallImagePath = "validateInput/src-small.png";
		String maskValidImagePath = "validateInput/mask-valid.png";	
		
		// Load all the images
		BufferedImage srcImage = ImageIO.read(new File(testImgPath + srcSmallImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath + maskValidImagePath));
		BufferedImage destImage = ImageIO.read(new File(testImgPath + dstImagePath));

		double average = 0;
		
		for(int i=0;i<10;i++) {
			// Setup the Poisson solver
			PoissonPhotomontage photomontage = new PoissonPhotomontage(srcImage, maskImage, destImage, new Point(95, 95));
			
			// Do the heavy lifting
			long t0 = System.nanoTime();
			photomontage.createPhotomontage();
			long t1 = System.nanoTime();
			
			average = (average + ((t1 - t0) / 1000000000)) / 2;
		}
		
		
		System.out.printf("Average %f s\r\n", average);
		// BiCG = 1.999023 s
		// CG = 1.107422 s
		// 
	}
}
