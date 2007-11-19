/*
 * Seamless Image Cloning Tools
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
 */

package ca.etsmtl.matrix;

import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.AbstractIterationMonitor;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;

public class MatrixSolverIterationMonitor extends AbstractIterationMonitor {
	private final int max;

	public MatrixSolverIterationMonitor(int max) {
		this.max = max;
	}

	protected boolean convergedI(final double r) throws IterativeSolverNotConvergedException {
		return iter >= max;
	}

	protected boolean convergedI(final double r, final Vector x) throws IterativeSolverNotConvergedException {
		return convergedI(r);
	}
}