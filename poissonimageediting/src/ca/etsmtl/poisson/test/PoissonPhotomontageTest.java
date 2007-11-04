package ca.etsmtl.poisson.test;


import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.etsmtl.poisson.PoissonPhotomontage;

public class PoissonPhotomontageTest {
	
	// top-level path
	private final String testImgPath = "resources/images/tests/"; 	

	// file references
	private final String dstImagePath = "validateInput/dst.png";
	private final String srcSmallImagePath = "validateInput/src-small.png";
	
	// the class being tested
	private PoissonPhotomontage poissonPhotomontage;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	// TODO tested method should be extracted into a class with several methods for each assert of this test method
	// Frank ton code pue
	@Test public void validateInputImages() {
		
		// Input valid data and expect correct result
		// imgSrc < imgDst
		// imgMask == imgSrc
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		
		poissonPhotomontage = new PoissonPhotomontage(dstImage,srcSmallImage);
		assertTrue(poissonPhotomontage.validateInputImages()); 
		
		// Input a too big source image and expect False
		
		// Input a the source image at a destination point that will have the source image go outside destination image and expect false
		
		// Input a mask that is not only composed of 1 and 0 and expect False
		
		// Input a source image and a mask of different size and expect False
		
		// Destination image must not have the mask pasted so that a mask value of 1 touches the destination image edges
		
		
		assertTrue(false);
		
		assertTrue(true);
	}
	
}
