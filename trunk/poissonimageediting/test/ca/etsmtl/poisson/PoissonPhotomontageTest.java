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
	private final String srcBigImagePath = "validateInput/src-toobig.png";
	private final String maskValidImagePath = "validateInput/mask-valid.png";
	private final String maskInvalidImagePath = "validateInput/mask-invalid.png";
	private final String maskFullImagePath = "validateInput/mask-full.png";
	
	// the class being tested
	private PoissonPhotomontage poissonPhotomontage;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test public void validateSourceImageSize() throws IOException {
		
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		// Input a too big source image : expect False
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		BufferedImage srcTooBigImage = ImageIO.read(new File(testImgPath+srcBigImagePath));
		
		poissonPhotomontage = new PoissonPhotomontage(srcTooBigImage,maskImage,dstImage,dstPt);
		assertTrue(!poissonPhotomontage.validateSourceImageSize());
	}
	
	@Test public void validateDestinationPosition() throws IOException {
		
		// Destination Point outside of dstImage : expect False
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		Point dstPt = new Point(300,150);
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertTrue(!poissonPhotomontage.validateDestinationPosition());
		
		// Input the source image at a destination point that will have the source image go outside destination image : expect false
		// FIXME we will have to test this, maybe the poisson editing accept that kind of stuff (will have to be reflected in the algorithm's implementation)
		// Make sure you blow the dstImage size
		// test  + src < max dst
		// testx + 128 < 286
		// testy + 100 < 218
		// This point should blow up dstImage
		dstPt = new Point(120,150);
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertTrue(!poissonPhotomontage.validateDestinationPosition());
	}
	
	@Test public void validateMask() throws IOException {
		
		// Input a source image and a mask of different size : expect False
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage invalidMaskImage = ImageIO.read(new File(testImgPath+maskInvalidImagePath));
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,invalidMaskImage,dstImage,dstPt);
		assertTrue(!poissonPhotomontage.validateMask());
		
		// Destination image must not have the mask pasted so that a mask value of 1 touches the destination image edges
		BufferedImage fullMaskImage = ImageIO.read(new File(testImgPath+maskFullImagePath));
		dstPt = new Point(0,0); // paste full mask on 0,0
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,fullMaskImage,dstImage,dstPt);
		assertTrue(!poissonPhotomontage.validateMask());
		
		// Send in a valid mask : expect True
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertTrue(poissonPhotomontage.validateMask());

	}
	
	@Test public void validateInputImages() throws IOException {
		
		// Input valid data and expect correct result
		// imgSrc < imgDst
		// imgMask == imgSrc
		// dst point inside target image
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertTrue(poissonPhotomontage.validateInputImages()); 
	}
	
}
