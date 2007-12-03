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

import static ca.etsmtl.photomontage.CustomAssert.assertBufferedImageEquals;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.exceptions.InvalidDestinationPositionException;
import ca.etsmtl.photomontage.exceptions.InvalidMaskException;
import ca.etsmtl.photomontage.exceptions.InvalidSourceImageSizeException;
import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;

/**
 * PoissonPhotomontageTest for testing the poisson algo
 *
 */
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
	private final String goatTestResult = "blackbox/goat-result.png";
	
	// the class being tested
	private PoissonPhotomontage poissonPhotomontage;

	/**
	 * @throws Exception if an error occurs
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws Exception if an error occurs
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Validate the source image size
	 * 
	 * @throws IOException if an error occurs
	 * @throws InvalidSourceImageSizeException 
	 */
	@Test public void validateSourceImageSize() throws IOException, InvalidSourceImageSizeException {
		
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		// Input a too big source image : expect False
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		BufferedImage srcTooBigImage = ImageIO.read(new File(testImgPath+srcBigImagePath));
		
		poissonPhotomontage = new PoissonPhotomontage(srcTooBigImage,maskImage,dstImage,dstPt);
		poissonPhotomontage.validateSourceImageSize();
	}
	
	/**
	 * Validate the destination position in the destination image
	 * 
	 * @throws IOException if an error occurs
	 * @throws InvalidDestinationPositionException 
	 */
	@Test public void validateDestinationPosition() throws IOException, InvalidDestinationPositionException {
		
		// Destination Point outside of dstImage : expect False
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		Point dstPt = new Point(300,150);
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		poissonPhotomontage.validateDestinationPosition();
		
		// Input the source image at a destination point that will have the source image go outside destination image : expect false
		// FIXME we will have to test this, maybe the poisson editing accept that kind of stuff (will have to be reflected in the algorithm's implementation)
		// Make sure you blow the dstImage size
		// test  + src < max dst
		// testx + 128 < 286
		// testy + 100 < 218
		// This point should blow up dstImage
		dstPt = new Point(120,150);
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		poissonPhotomontage.validateDestinationPosition();
	}
	
	/**
	 * Validate the mask
	 * 
	 * @throws IOException if an error occurs
	 * @throws InvalidMaskException 
	 */
	@Test public void validateMask() throws IOException, InvalidMaskException {
		
		// Input a source image and a mask of different size : expect False
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage invalidMaskImage = ImageIO.read(new File(testImgPath+maskInvalidImagePath));
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,invalidMaskImage,dstImage,dstPt);
		poissonPhotomontage.validateMask();
		
		// Destination image must not have the mask pasted so that a mask value of 1 touches the destination image edges
		BufferedImage fullMaskImage = ImageIO.read(new File(testImgPath+maskFullImagePath));
		dstPt = new Point(0,0); // paste full mask on 0,0
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,fullMaskImage,dstImage,dstPt);
		poissonPhotomontage.validateMask();
		
		// Send in a valid mask : expect True
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		poissonPhotomontage.validateMask();

	}
	
	/**
	 * Validate the input images
	 * 
	 * @throws IOException if an error occurs
	 * @throws InvalidMaskException 
	 * @throws InvalidDestinationPositionException 
	 * @throws InvalidSourceImageSizeException 
	 */
	@Test public void validateInputImages() throws IOException, InvalidSourceImageSizeException, InvalidDestinationPositionException, InvalidMaskException {
		
		// Input valid data and expect correct result
		// imgSrc < imgDst
		// imgMask == imgSrc
		// dst point inside target image
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		Point dstPt = new Point(15,21); // arbitrary valid values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		poissonPhotomontage.validateInputImages(); 
	}
	
	/**
	 * These tests were made using an external Poisson editing implementation. 
	 * We used a MatLab implementation located in ext/poisson_matlab/main and 
	 * mainchevre. Then we compare each pixel of createPhotomontage's resulting 
	 * image and the matlab program's with a certain tolerance level.  
	 * 
	 * @throws IOException if an in/output error occurs
	 * @throws ComputationException if an computation error occurs
	 * @throws IterativeSolverNotConvergedException if the algo dont converged
	 * @throws InvalidMaskException 
	 * @throws InvalidDestinationPositionException 
	 * @throws InvalidSourceImageSizeException 
	 */
	@Test public void createPhotoMontage() throws IOException, ComputationException, IterativeSolverNotConvergedException, InvalidSourceImageSizeException, InvalidDestinationPositionException, InvalidMaskException {
		
		BufferedImage srcSmallImage = ImageIO.read(new File(testImgPath+srcSmallImagePath));
		BufferedImage dstImage = ImageIO.read(new File(testImgPath+dstImagePath));
		BufferedImage maskImage = ImageIO.read(new File(testImgPath+maskValidImagePath));
		
		// MatLab's result
		BufferedImage matlabResult = ImageIO.read(new File(testImgPath+goatTestResult));				
		
		// Insertion point of 95,95 in our external test scenario
		Point dstPt = new Point(95,95); // arbitrary valid values
		
		poissonPhotomontage = new PoissonPhotomontage(srcSmallImage,maskImage,dstImage,dstPt);
		assertBufferedImageEquals(matlabResult, poissonPhotomontage.createPhotomontage(), 5);
	}
	
}
