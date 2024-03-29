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

import java.awt.image.BufferedImage;
import static org.junit.Assert.fail;
import static ca.etsmtl.photomontage.util.ColorChannel.*;

/**
 * Custom Assert for the unit tests
 *
 */
public class CustomAssert {

	/**
	 * A BufferedImage comparison. Validates image size to be equal, not null 
	 * images and each value of each pixel to be equal within a certain range.
	 * @param expected expected image
	 * @param actual actual image
	 * @param delta range of acceptable per pixel value derivation
	 */
	public static void assertBufferedImageEquals(BufferedImage expected, BufferedImage actual, int delta) {
		
		// If one of the image is null, this doesn't pass the assertion
		if (expected == null || actual == null) 
			fail("One of the tested image is null");
		
		// If images are not of the same size, they don't pass the assertion
		if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight())
			fail("Images are not of the same size");
		
		// Iterate through the image and validate unique 
		for(int x=0; x < expected.getWidth()-1; x++) {
			for(int y=0; y < expected.getHeight()-1; y++) {
				
				//TODO I could iterate through 
				
				// For Alpha
				assertEquals((expected.getRGB(x, y) & ALPHA.mask()) >> ALPHA.shift(),
				 			 (actual.getRGB(x, y) & ALPHA.mask()) >> ALPHA.shift(), delta);
				
				// For Red
				assertEquals((expected.getRGB(x, y) & RED.mask()) >> RED.shift(),
							 (actual.getRGB(x, y) & RED.mask()) >> RED.shift(), delta);

				// For Green
				assertEquals((expected.getRGB(x, y) & GREEN.mask()) >> GREEN.shift(),
							 (actual.getRGB(x, y) & GREEN.mask()) >> GREEN.shift(), delta);
				
				// For Blue
				assertEquals((expected.getRGB(x, y) & BLUE.mask()) >> BLUE.shift(),
							 (actual.getRGB(x, y) & BLUE.mask()) >> BLUE.shift(), delta);
				
			}
		}
	}
	
	/**
	 * Expect two integers to be equal within a specified delta
	 * @param message message for the assertion error
	 * @param expected the expected value
	 * @param actual the value checked against expected
	 * @param delta a range of tolerance
	 */
	public static void assertEquals(String message, int expected, int actual, int delta) {
		
		if (expected == actual)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			fail(message+"\nExpected: "+expected+" got "+actual+".");
	}

	/**
	 * Expect two integers to be equal within a specified delta
	 * @param expected the expected value
	 * @param actual the value checked against expected
	 * @param delta a range of tolerance
	 */
	public static void assertEquals(int expected, int actual, int delta) {
		assertEquals(null, expected, actual, delta);
	}
}
