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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.exceptions.InvalidDestinationPositionException;
import ca.etsmtl.photomontage.exceptions.InvalidMaskException;
import ca.etsmtl.photomontage.exceptions.InvalidSourceImageSizeException;
import ca.etsmtl.photomontage.poisson.PoissonPhotomontage;

/**
 * 
 * @author ag95300
 *
 */
public class BatchTesting {
	/**
	 * Main
	 * @param args
	 * @throws IOException
	 * @throws ComputationException
	 * @throws InvalidMaskException 
	 * @throws InvalidDestinationPositionException 
	 * @throws InvalidSourceImageSizeException 
	 */
	public static void main(String args[]) throws IOException, ComputationException, InvalidSourceImageSizeException, InvalidDestinationPositionException, InvalidMaskException {
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
