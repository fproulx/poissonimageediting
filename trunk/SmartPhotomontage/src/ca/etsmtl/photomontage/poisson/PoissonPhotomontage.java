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
 */

package ca.etsmtl.photomontage.poisson;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.CG;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import ca.etsmtl.photomontage.AbstractPhotomontage;
import ca.etsmtl.photomontage.exceptions.ComputationException;
import ca.etsmtl.photomontage.exceptions.InvalidDestinationPositionException;
import ca.etsmtl.photomontage.exceptions.InvalidMaskException;
import ca.etsmtl.photomontage.exceptions.InvalidSourceImageSizeException;
import ca.etsmtl.photomontage.matrix.MatrixCell;
import ca.etsmtl.photomontage.matrix.MatrixSolverIterationMonitor;
import ca.etsmtl.photomontage.util.ColorChannel;

/**
 * This is an implementation of <a href="http://research.microsoft.com/vision/cambridge/papers/perez_siggraph03.pdf">the "Poisson Image Editing" algorithm</a>.
 *  
 * @author fproulx <francois.proulx@gmail.com>
 * @since 1.0
 */
public class PoissonPhotomontage extends AbstractPhotomontage {
	/**
	 * The target number of iterations for the iterative matrix solver.
	 */
	private static final int SOLVER_ITERATIONS = 300;
	/**
	 * The bitmask representing the non-selected parts of the mask as a set of ARGB integer pixels.
	 * The pixels MUST be set to pure black RGB (0,0,0) with a 0.0f alpha channel (fully transparent)
	 */
	private static final int MASK_BACKGROUND = 0x00000000;
	/**
	 * The bitmask for opaque pixels (the value for the alpha channel is 0xFF).
	 */
	private static final int OPAQUE_BACKGROUND = 0xFF000000;

	/**
	 * Constructs a concrete implementation of {@code Photomontage} 
	 * and sets the required fields to compute the resulting image.
	 * 
	 * @param srcImage The Source image (to be seamlessly cloned in the {@code Photomontage}).
	 * @param maskImage The Mask image used to specify the pixels of the Source image to be used.
	 * @param destImage The Destination image onto which the Source image will be cloned.
	 * @param destPosition The target position of the cloned image.
	 */
	public PoissonPhotomontage(BufferedImage srcImage, BufferedImage maskImage, BufferedImage destImage, Point destPosition) {
		super(srcImage, maskImage, destImage, destPosition);
	}
	
	/**
	 * Adds a new coefficient to the "Poisson equation with Dirichlet boundary conditions" 
	 * in the list of cells to be included in the matrix used to compute the solution to the photomontage.
	 *  
	 * @param matrixDataList The list of rows for the matrix. 
	 * @param rhsVector The right-hand side vector (i.e. b in Ax = b).
	 * @param solutionRow The current solution matrix row number.
	 * @param destSolutionsMap The mapping between destination pixels and the solution matrix row number.
	 * @param x The current pixel (x).
	 * @param y The current pixel (y).
	 * @param xDest The pixel in the destination image (x).
	 * @param yDest The pixel in the destination image (y).
	 * @param xDelta The delta (either -1, - or +1), designating the neighboring pixel to check (x).
	 * @param yDelta The delta (either -1, - or +1), designating the neighboring pixel to check (y).
	 * @param channel The {@code ColorChannel} instance (RED, GREEN or BLUE) to use.
	 */
	protected void addPoissonEquationCoefficientsToMatrix(List<MatrixCell> matrixDataList, Vector rhsVector, int solutionRow, Map<Integer, Integer> destSolutionsMap, int x, int y, int xDest, int yDest, int xDelta, int yDelta, ColorChannel channel) {
		// Check if the 4 neighboring pixels around (x,y) (i.e. top, right, bottom, left) are also part of the selected portion of the mask.
		if(maskImage.getRGB(x + xDelta, y + yDelta) != MASK_BACKGROUND) {
			// Add a new cell to the matrix, as part of the equation. 
			// Notice that because we check 4 neighbors, there can be up to 5 coefficients per solution row.
			matrixDataList.add(new MatrixCell(solutionRow, destSolutionsMap.get(destImage.getWidth() * (yDest + yDelta) + (xDest + xDelta)), -1));
		}
		else {
			// Add the color intensities (by channel) to the right-hand side vector.
			// Notice the the add() method uses the compound addition operator (i.e. rightHandSide[solutionRow] += value).
			rhsVector.add(solutionRow, (destImage.getRGB(xDest + xDelta, yDest + yDelta) & channel.mask()) >> channel.shift());
		}
	}
	
	/**
	 * Clamps the given value between 0 and 255 (to fit in an integer ARGB pixel)
	 * 
	 * @return The clamped value.
	 */
	protected int clampValue(int value) {
		int newValue = value;
         if (value < 0)
        	 newValue = 0;
         else if (value > 255)
        	 newValue = 255;
	      return newValue;
	}

	/**
	 * Creates the actual resulting photomontage image out of the resulting solution vectors.
	 * 
	 * @return The resulting image in the form of a BufferedImage instance.
	 * @throws ComputationException
	 * @throws InvalidMaskException 
	 * @throws InvalidDestinationPositionException 
	 * @throws InvalidSourceImageSizeException 
	 */
	public BufferedImage createPhotomontage() throws ComputationException, InvalidSourceImageSizeException, InvalidDestinationPositionException, InvalidMaskException {
		// Make sure the input images fit the requirements set by the algorithm.
		validateInputImages();
		
	    // Build a mapping between points in the destination image and the computed solutions.
	    ConcurrentHashMap<Integer, Integer> destToSolutionsMap = (ConcurrentHashMap<Integer, Integer>) createSolutionsMap();
	    
	    try {
	    	// Prepare a parralel task executor for the 3 solvers (red, green, blue)
	    	Executor executor = Executors.newFixedThreadPool(3); 
	    	// Prepare a parallel execution barrier
	    	CountDownLatch barrier = new CountDownLatch(3);
	    	
	    	// Prepare and launch threads for each of the computation tasks
	    	FutureTask<Vector> computeTaskVectorRed = solvePoissonEquationsForChannel(destToSolutionsMap, ColorChannel.RED, barrier);
	    	executor.execute(computeTaskVectorRed);
	    	FutureTask<Vector> computeTaskVectorGreen = solvePoissonEquationsForChannel(destToSolutionsMap, ColorChannel.GREEN, barrier);
	    	executor.execute(computeTaskVectorGreen);
	    	FutureTask<Vector> computeTaskVectorBlue = solvePoissonEquationsForChannel(destToSolutionsMap, ColorChannel.BLUE, barrier);
	    	executor.execute(computeTaskVectorBlue);
	    	
	    	// Wait for all the tasks to be completed.
			barrier.await();
	    	
			// Retrieve the computed values
			Vector solutionsVectorRed = computeTaskVectorRed.get();
			Vector solutionsVectorGreen = computeTaskVectorGreen.get();
			Vector solutionsVectorBlue = computeTaskVectorBlue.get();
			
			// Create a perfect copy of the destination that will be used as a canvas for the final composition.
			BufferedImage finalImage = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) finalImage.getGraphics();
			g2d.drawImage(destImage, 0, 0, null);

			// For each pixel in the cloned image (source image).
			int xDest, yDest;
			int rgb;
			int wDest = destImage.getWidth();
			for (int x = 1; x < srcImage.getWidth() - 1; x++) {
				for (int y = 1; y < srcImage.getHeight() - 1; y++) {
					if (maskImage.getRGB(x, y) != MASK_BACKGROUND) {
						// Move to the corresponding position in the destination image.
						xDest = x + destPosition.x;
						yDest = y + destPosition.y;
						
						// Add up all the channels to compute the value of the pixel (while making sure the values fit in a byte so that they don't collide when shifted).
						rgb = clampValue((int) Math.round(solutionsVectorRed.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.RED.shift() |
						      clampValue((int) Math.round(solutionsVectorGreen.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.GREEN.shift() |
						      clampValue((int) Math.round(solutionsVectorBlue.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.BLUE.shift() |
						      OPAQUE_BACKGROUND;
						
						// Replace the original pixel with the computed one.
						finalImage.setRGB(xDest, yDest, rgb);
					}
				}
			}
			
			// If everything went as expected, return the computed image.
			return finalImage;
	    }
	    catch(IterativeSolverNotConvergedException e) {
	    	throw new ComputationException("The iterative matrix solver could not converge to the solution.");
	    } catch (InterruptedException e) {
	    	throw new ComputationException("The parralel computation tasks got abrutly interrupted.");
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new ComputationException("The parralel execution of a task got abrutly interrupted.");
		}
	}
	
	/**
	 * Create a mapping between the pixels in the destination image and the row of the matrix containing the solution.
	 * 
	 * @return The mapping.
	 */
	protected Map<Integer, Integer> createSolutionsMap() {
		int N = 0;
		ConcurrentHashMap<Integer, Integer> destToSolutionsMap = new ConcurrentHashMap<Integer, Integer>();
		for (int x = 1; x < srcImage.getWidth() - 1; x++) {
			for (int y = 1; y < srcImage.getHeight() - 1; y++) {
				if (maskImage.getRGB(x, y) != MASK_BACKGROUND) {
					destToSolutionsMap.put(destImage.getWidth() * (y + destPosition.y) + (x + destPosition.x), N);
					// Count the total number of rows in the matrix
					N++;
				}
			}
	    }
		return destToSolutionsMap;
	}
	
	/**
	 * Compute the divergence about a point in an image (over a given field)
	 * 
	 * @param img
	 * @param channel
	 * @param x
	 * @param y
	 */
	protected int computeDivergenceAboutPoint(final BufferedImage img, final ColorChannel channel, final int x, final int y) {
		return - ((img.getRGB(x - 1, y) & channel.mask()) >> channel.shift()) + 
		       - ((img.getRGB(x, y - 1) & channel.mask()) >> channel.shift()) + 
		       - ((img.getRGB(x + 1, y) & channel.mask()) >> channel.shift()) + 
		       - ((img.getRGB(x, y + 1) & channel.mask()) >> channel.shift()) 
		       + 4 * ((img.getRGB(x, y) & channel.mask()) >> channel.shift());
	}
	
	/**
	 * 
	 * @return destination image
	 */
	public BufferedImage getDestinationImage() {
		return destImage;
	}
	
	/**
	 * 
	 * @return destination position
	 */
	public Point2D getDestinationPosition() {
		return destPosition;
	}
	
	/**
	 * 
	 * @return mask image
	 */
	public BufferedImage getMaskImage() {
		return maskImage;
	}
	
	/**
	 * 
	 * @return source image
	 */
	public BufferedImage getSourceImage() {
		return srcImage;
	}
	
	/**
	 * Run the iterative matrix solver for a given ColorChannel.
	 *  
	 * @param destToSolutionsMap The mapping.
	 * @param channel The color channel to compute.
	 * 
	 * @return A FutureTask that will compute the solution vector (containing the computed pixel values).
	 * @throws ComputationException
	 * @throws IterativeSolverNotConvergedException
	 */
	protected FutureTask<Vector> solvePoissonEquationsForChannel(final Map<Integer, Integer> destToSolutionsMap, final ColorChannel channel, final CountDownLatch doneSignal) throws ComputationException, IterativeSolverNotConvergedException {
		return new FutureTask<Vector>(new Callable<Vector>() {
			public Vector call() throws ComputationException, IterativeSolverNotConvergedException {
				System.out.println("Called " + channel);
				// Get the number of rows in the squared matrix. 
				int N = destToSolutionsMap.size();
			    
				// This array will be used to prepare the sparse matrix (initial size N).
				List<MatrixCell> matrixDataList = new ArrayList<MatrixCell>(N);
					    
				// Prepare the right-hand side vector, that will contain the conditions (i.e. Ax=b).
				Vector rhsVector = new DenseVector(N);
				
				// A counter to keep track of the solution row to check.
				int solutionRow = 0;
				int xDest, yDest;
				// This will contain the divergence field
				int div;
				
				// For each pixel in the cloned image (source image).
				for(int x = 1; x < srcImage.getWidth() - 1; x++) {
					for(int y = 1; y < srcImage.getHeight() - 1; y++) {
						if(maskImage.getRGB(x, y) != MASK_BACKGROUND) {
							// Move to the corresponding position in the destination image
							xDest = x + destPosition.x;
							yDest = y + destPosition.y;
							
							// Add the coefficients of the Poisson equations, as needed, for each of the four neighbors.
							
							// Top neighbor
							addPoissonEquationCoefficientsToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, 0, -1, channel);
							// Left neighbor
							addPoissonEquationCoefficientsToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, -1, 0, channel);
							// Bottom neighbor
							addPoissonEquationCoefficientsToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, 0, +1, channel);
							// Right neighbor
							addPoissonEquationCoefficientsToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, +1, 0, channel);

							// Set the coefficient on the diagonal
							matrixDataList.add(new MatrixCell(solutionRow, solutionRow, 4));
							
							// Compute the divergences about the current point for the source and destination
							div = computeDivergenceAboutPoint(srcImage, channel, x, y);

							// Construct the guidance field by adding the color intensity (by channel) to the right-hand side vector.
							// Notice the the add() method uses the compound addition operator (i.e. rightHandSide[solutionRow] += value).
							rhsVector.add(solutionRow, div);

							// Increment the counter to the next row.
							solutionRow++;
						}
					}
				}
				
				System.out.println("Done " + channel);
				
				// Make sure that all the expected rows were dealt with.
				if(solutionRow != N)
					throw new ComputationException(String.format("The number of rows did not match the expected number. (solutionRow != N) --> (%d != %d) ", solutionRow, N));
				
				// Build an array containing a list of non-zero columns for the sake of feeding it to the sparse matrix
		        List<Set<Integer>> nonZeroRows = new ArrayList<Set<Integer>>(N);
		        for (int i = 0; i < N; ++i)
		        	nonZeroRows.add(new HashSet<Integer>());

				for(MatrixCell cell: matrixDataList)
					nonZeroRows.get(cell.row).add(cell.col);

		        int[][] nonZeroCells = new int[N][];
		        for (int i = 0; i < N; ++i) {
		        	nonZeroCells[i] = new int[nonZeroRows.get(i).size()];
		            int j = 0;
		            for (Integer colind : nonZeroRows.get(i))
		            	nonZeroCells[i][j++] = colind;
		        }
				
		        System.out.println("Filling " + channel);
				// Prepare a NxN sparse matrix, that will contain the system linear of equations.
				Matrix A = new CompRowMatrix(N, N, nonZeroCells);
				
				// Fill the matrix the actual values
				for(MatrixCell cell: matrixDataList)
					A.set(cell.row, cell.col, cell.value);
				
				// Prepare the solution vector that will contain the value of each computed pixel
				// Shuffle some random data around to speed up the convergence of the solution.
				Vector solutionsVector = Matrices.random(N);

				System.out.println("Solving " + channel);
			    // Choose a Conjugate Gradient iterative solver to compute Ax = b
			    IterativeSolver solver = new CG(solutionsVector);
			    
			    // Limit the solver iterations by setting up a custom monitor
			    solver.setIterationMonitor(new MatrixSolverIterationMonitor(SOLVER_ITERATIONS));
			    
			    // Start the iterative solver
			    solver.solve(A, rhsVector, solutionsVector);
			    System.out.println("Done " + channel);
				// Notify the synchronization primitive that the computation is completed.
				doneSignal.countDown();
				System.out.println("Counted down " + channel);
				return solutionsVector;
			}
			
		});
	}
	
	/**
	 * Validate the destination position.
	 * 
	 * @return true: valid / false: invalid
	 */
	public void validateDestinationPosition() throws InvalidDestinationPositionException {
		// Make sure that the specified destination offset fits
		// the solver requirements and is inside the destination image.
		if(destPosition == null || 
		   destPosition.x <= 0 ||
		   destPosition.y <= 0 ||
		   destPosition.x >= destImage.getWidth() -1 ||
		   destPosition.y >= destImage.getHeight() -1)
			throw new InvalidDestinationPositionException("The destination position MUST within the boundaries of the destination image.");
		
		// Destination Point + Source image must not be taller than Destination Image
		if ((destPosition.x + srcImage.getWidth())  > destImage.getWidth() ||
			(destPosition.y + srcImage.getHeight()) > destImage.getHeight())
			throw new InvalidDestinationPositionException("The entire source image MUST be constrained inside the destination image.");
	}

	/**
	 * Validate the input images against all the requirements.
	 * 
	 * @return true: valid / false: invalid
	 * @throws InvalidMaskException 
	 * @throws InvalidDestinationPositionException 
	 * @throws InvalidSourceImageSizeException 
	 */
	public void validateInputImages() throws InvalidSourceImageSizeException, InvalidDestinationPositionException, InvalidMaskException {
		validateSourceImageSize();
		validateDestinationPosition();
		validateMask();
	}
	
	/**
	 * Validate the mask image.
	 * 
	 * @return true: valid / false: invalid
	 */
	public void validateMask() throws InvalidMaskException {
		if (maskImage == null || destImage == null)
			throw new InvalidMaskException("The mask or the destination image was null.");
		
		// Make sure that the mask fits in the destination area
	    if(destImage.getWidth() < maskImage.getWidth() || 
	 	   destImage.getHeight() < maskImage.getHeight())
	    	throw new InvalidMaskException("The mask MUST be smaller than the destination image.");
	    
		// Input a source image and a mask of different size
	    if(srcImage.getWidth() != maskImage.getWidth() ||
	       srcImage.getHeight() != maskImage.getHeight())
	    	throw new InvalidMaskException("The mask MUST have the same size as the source image.");
	    
	    /*
		// Destination image must not have the mask pasted so that a mask value of 0 touches the destination image edges
	    // verify for non-zeros values on top and bottom edges 
	    for(int x=0; x<maskImage.getWidth()-1; x++) {
	    	// Alpha 255 Red 0 Green 0 Blue 0 => MASK_BACKGROUND
	    	if (maskImage.getRGB(x, 0) != MASK_BACKGROUND ||
	    		maskImage.getRGB(x, maskImage.getHeight()-1) != MASK_BACKGROUND)
	    		return false;
	    }
	    	
	    // verify for non-zeros values on left and right edges 
    	for(int y=0; y<maskImage.getHeight()-1; y++) {
	    	// Alpha 255 Red 0 Green 0 Blue 0 => MASK_BACKGROUND
    		if (maskImage.getRGB(0, y) != MASK_BACKGROUND ||
		    	maskImage.getRGB(maskImage.getWidth()-1, y) != MASK_BACKGROUND)
		    		return false;
    	}
	    */
	}
	
	/**
	 * Validate Source Image correctness.
	 * 
	 * @return true: valid false: invalid
	 */
	public void validateSourceImageSize() throws InvalidSourceImageSizeException {
		if (srcImage == null)
			throw new InvalidSourceImageSizeException("The source image was null.");

		// Source must be smaller or equal than the destination image
		if (srcImage.getWidth() > destImage.getWidth() ||
			srcImage.getHeight() > destImage.getHeight()) {
			throw new InvalidSourceImageSizeException("The source image MUST be smaller than the destination image.");
		}
	}
}
	