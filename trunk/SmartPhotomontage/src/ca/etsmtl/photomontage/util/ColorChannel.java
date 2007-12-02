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

package ca.etsmtl.photomontage.util;

/**
 * This is an enumeration of the bitmasks and bitshifts for each integer ARGB pixels.
 * @author fproulx
 *
 */
public enum ColorChannel {
	 RED (0x00FF0000, 16),
	 GREEN (0x0000FF00, 8),
	 BLUE (0x000000FF, 0),
	 ALPHA (0xFF000000, 24);
	 
	 private final int mask, shift;
	 ColorChannel(int mask, int shift) {
		 this.mask = mask;
		 this.shift = shift;
	 }
	 
	 public int mask() { return mask; }
	 public int shift() { return shift; }
}