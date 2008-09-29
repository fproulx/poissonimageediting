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

package ca.etsmtl.photomontage.exceptions;

/**
 * ComputationException, exception for computation error
 *
 */
public class ComputationException extends Exception {
	private static final long serialVersionUID = -1982069004778632902L;
	
	/**
	 * Constructor
	 * @param msg
	 */
	public ComputationException(String msg) {
		super("The computation could not complete successfully because of: \r\n" + msg);
	}
}
