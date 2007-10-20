package de.berlios.ressim;

import java.io.*;
import java.util.zip.*;

import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.io.*;
import no.uib.cipr.matrix.sparse.*;

/**
 * Sparse iterative solver benchmark. To compile and run it, type:
 *  javac -classpath mtj.jar SparseIterSolve.java
 *  java -cp mtj.jar:. SparseIterSolve {format} {solver} {matrixfile} {iterations} [preconditioner]
 *
 * The matrix must be in either the Matrix Market exchange format or
 * the coordinate format.
 */
public class SparseIterSolve {

  public static void main(String[] args) throws IOException, IterativeSolverNotConvergedException {

    if (args.length < 4 || args.length > 5) {
      System.err.println("Usage: {format} {solver} {matrixfile} {iterations} [preconditioner]");
      System.err.println("\nThe format is one of: Dense, CRS, CCS, or CDS");
      System.err.println("The solver is any of the iterative solvers");
      System.err.println("The preconditioner is any of the preconditioners");
      return;
    }

    String format = args[0];
    String solver = args[1];
    String file = args[2];
    int iters = Integer.parseInt(args[3]);

    // Create a matrix reader. If the file is compressed,
    // use on the fly decompression
    MatrixVectorReader reader = null;
    if (file.endsWith("gz"))
      reader = new MatrixVectorReader
        (new InputStreamReader
          (new GZIPInputStream
            (new FileInputStream(file))));
    else
      reader = new MatrixVectorReader(new FileReader(file));

    // Read in the matrix
    Matrix A = null;
    if (format.equals("Dense"))
      A = new DenseMatrix(reader);
    else if (format.equals("CRS"))
      A = new CompRowMatrix(reader);
    else if (format.equals("CCS"))
      A = new CompColMatrix(reader);
    else if (format.equals("CDS"))
      A = new CompDiagMatrix(reader);
    else
      throw new IllegalArgumentException("Unsupported matrix format " + format);

    // Create vectors. Set the right hand side such that the solution is unity
    Vector b = new DenseVector(A.numColumns());
    Vector x = new DenseVector(A.numRows());
    for (int i=0; i<A.numColumns(); ++i)
      x.set(i, 1);
    A.mult(x, b);
    x = Matrices.random(x.size());

    // Create the solver
    IterativeSolver solv = null;
    if (solver.equals("BiCG"))
      solv = new BiCG(x);
    else if (solver.equals("BiCGstab"))
      solv = new BiCGstab(x);
    else if (solver.equals("CG"))
      solv = new CG(x);
    else if (solver.equals("CGS"))
      solv = new CGS(x);
    else if (solver.equals("GMRES"))
      solv = new GMRES(x);
    else if (solver.equals("IR"))
      solv = new IR(x);
    else if (solver.equals("QMR"))
      solv = new QMR(x);
    else
      throw new IllegalArgumentException("Unsupported solver " + solver);

    // Optionally create a preconditioner
    Preconditioner M = solv.getPreconditioner();
    if (args.length == 5) {
      String prec = args[4];
      if (prec.equals("Diagonal"))
        M = new DiagonalPreconditioner(A.numRows());
      else if (prec.equals("SSOR"))
        M = new SSOR(new CompRowMatrix(A));
      else if (prec.equals("ICC"))
        M = new ICC(new CompRowMatrix(A));
      else if (prec.equals("ILU"))
        M = new ILU(new CompRowMatrix(A));
      else if (prec.equals("ILUT"))
        M = new ILUT(new FlexCompRowMatrix(A));
      else if (prec.equals("AMG"))
        M = new AMG();
    }

    // Set up the preconditioner, and report on the setup time
    long t0 = System.currentTimeMillis();
    M.setMatrix(A);
    long t1 = System.currentTimeMillis();

    double psec = (t1-t0)/1000.;

    System.out.println("Seconds used creating preconditioner:\t" + psec);

    // Attach the preconditioner
    solv.setPreconditioner(M);

    // Set the number of iterations, and report on the progress
    solv.setIterationMonitor(new SimpleIterationMonitor(iters));
    solv.getIterationMonitor().setIterationReporter
      (new OutputIterationReporter());

    // Start the solver
    t0 = System.currentTimeMillis();
    solv.solve(A, b, x);
    t1 = System.currentTimeMillis();

    double itps = iters / ((t1-t0)/1000.);

    System.out.println("Iterations per second:\t" + itps);
  }

  /**
   * Simple iteration monitor. Stops the iteration after the given number of iterations
   */
  private static class SimpleIterationMonitor extends AbstractIterationMonitor {

    private int max;

    SimpleIterationMonitor(int max) {
      this.max = max;
    }

    protected boolean convergedI(double r, Vector x) throws IterativeSolverNotConvergedException {
      return convergedI(r);
    }

    protected boolean convergedI(double r) throws IterativeSolverNotConvergedException {
      return iter >= max;
    }
  }

}
