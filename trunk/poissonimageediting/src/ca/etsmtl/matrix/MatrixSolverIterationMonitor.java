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