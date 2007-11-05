package ca.etsmtl.poisson;


import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	private final String maskValidImagePath = "validateInput/mask-valid.png";
	
	// the class being tested
	private PoissonPhotomontage poissonPhotomontage;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	// TODO tested method should be extracted into a class with several methods for each assert of this test method
	@Test public void validateInputImages() throws IOException {
		
		// Input valid data and expect correct result
		// imgSrc < imgDst
		// imgMask == imgSrc
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		Point dstPt = new Point(15,21); // arbitrary values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertTrue(poissonPhotomontage.validateInputImages()); 
		
		// Input a too big source image : expect False
		
		// Destination Point outside of dstImage
		
		// Input the source image at a destination point that will have the source image go outside destination image : expect false
		// FIXME we will have to test this, maybe the poisson editing accept that kind of stuff (will have to be reflected in the algorithm's implementation)
		
		// Input a mask that is not only composed of 1 and 0 and expect False
		
		// Input a source image and a mask of different size and expect False
		
		// Destination image must not have the mask pasted so that a mask value of 1 touches the destination image edges
		
	}
	
}
