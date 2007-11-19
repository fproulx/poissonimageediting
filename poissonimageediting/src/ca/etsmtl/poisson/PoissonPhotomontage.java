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

package ca.etsmtl.poisson;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.io.MatrixInfo;
import no.uib.cipr.matrix.io.MatrixSize;
import no.uib.cipr.matrix.io.MatrixVectorReader;
import no.uib.cipr.matrix.io.MatrixVectorWriter;
import no.uib.cipr.matrix.sparse.BiCG;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import ca.etsmtl.matrix.MatrixCell;
import ca.etsmtl.matrix.MatrixSolverIterationMonitor;
import ca.etsmtl.poisson.exceptions.ComputationException;
import ca.etsmtl.util.ColorChannel;

import com.Ostermiller.util.CircularByteBuffer;

/**
 * This is an implementation of the "Poisson Image Editing" algorithm
 * {@link http://research.microsoft.com/vision/cambridge/papers/perez_siggraph03.pdf} 
 * 
 * @author François Proulx <francois.proulx@gmail.com>
 * @since 1.0
 */
public class PoissonPhotomontage implements Photomontage {
	/**
	 * The target number of iterations for the iterative matrix solver.
	 */
	private static final int SOLVER_ITERATIONS = 300;
	/**
	 * The bitmask representing the non-selected parts of the mask as a set of ARGB integer pixels.
	 */
	private static final int MASK_BACKGROUND = 0xFF000000;
	/**
	 * The bitmask for opaque pixels (the value for the alpha channel is 0xFF).
	 */
	private static final int OPAQUE_BACKGROUND = 0xFF000000;
	/**
	 * The Source, Mask and Destination images which will be used to create the {@code Photomontage}.
	 */
	private final BufferedImage srcImage, maskImage, destImage;
	/**
	 * The target position of the cloned image in the destination image.  
	 */
	private final Point destPosition;

	/**
	 * Constructs a {@code Photomontage} and sets the required fields to compute the resulting image.
	 * 
	 * @param srcImage The Source image (to be seamlessly cloned in the {@code Photomontage}).
	 * @param maskImage The Mask image used to specify the pixels of the Source image to be used.
	 * @param destImage The Destination image onto which the Source image will be cloned.
	 * @param destPosition The target position of the cloned image.
	 */
	public PoissonPhotomontage(final BufferedImage srcImage, final BufferedImage maskImage, final BufferedImage destImage, final Point destPosition) {
		this.srcImage = srcImage;
		this.maskImage = maskImage;
		this.destImage = destImage;
		this.destPosition = destPosition;
	}

	/**
	 * 
	 * @param matrixDataList
	 * @param rhsVector
	 * @param solutionRow
	 * @param solutionsMap
	 * @param x
	 * @param y
	 * @param xDest
	 * @param yDest
	 * @param xDelta
	 * @param yDelta
	 * @param channel
	 */
	public void addPoissonEquationToMatrix(List<MatrixCell> matrixDataList, Vector rhsVector, int solutionRow, Map<Integer, Integer> solutionsMap, int x, int y, int xDest, int yDest, int xDelta, int yDelta, ColorChannel channel) {
		if(maskImage.getRGB(x + xDelta, y + yDelta) != MASK_BACKGROUND) {
			// This pixel is already used, get the diagonal position of the pixel
			matrixDataList.add(new MatrixCell(solutionRow, solutionsMap.get(destImage.getWidth() * (yDest + yDelta) + (xDest + xDelta)), -1));
		}
		else {
			// rightHandSide[solutionRow] += value
			rhsVector.add(solutionRow, (destImage.getRGB(xDest, yDest - 1) & channel.mask()) >> channel.shift());
		}
	}

	public BufferedImage createPhotomontage() throws ComputationException {
		// Make sure the input images fit the requirements
		if(validateInputImages()) {
		    // Build a mapping between points in the destination image and the computed solutions
		    ConcurrentHashMap<Integer, Integer> destToSolutionsMap = (ConcurrentHashMap<Integer, Integer>) createSolutionsMap();
		    
		    // Prepare a 3x3 Laplacian kernel for 2D convolution
		    final Kernel laplacian = new Kernel(3, 3, 
		    		                      new float[] { 0, -1,  0,
		    		                                   -1,  4, -1,
		    		                                    0, -1,  0});
		    // Prepare a 2D Laplacian convolution, don't compute the edges
		    final ConvolveOp laplacianConv = new ConvolveOp(laplacian, ConvolveOp.EDGE_NO_OP, null);
		    // Compute the divergence of the destination image (i.e. by applying the Laplacian kernel)
		    BufferedImage destDivergence = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		    laplacianConv.filter(destImage, destDivergence);
		    
		    try {
				Vector solutionsVectorRed = solvePoissonEquationsForChannel(destToSolutionsMap, destDivergence, ColorChannel.RED);
				Vector solutionsVectorGreen = solvePoissonEquationsForChannel(destToSolutionsMap, destDivergence, ColorChannel.GREEN);
				Vector solutionsVectorBlue = solvePoissonEquationsForChannel(destToSolutionsMap, destDivergence, ColorChannel.BLUE);
				
				// Copy the destination into the montage (the background)
				BufferedImage finalImage = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) finalImage.getGraphics();
				g2d.drawImage(destImage, 0, 0, null);

				// For each pixel in the cloned image (source image)
				int xDest, yDest;
				int rgb;
				int wDest = destImage.getWidth();
				for (int x = 1; x < srcImage.getWidth() - 1; x++) {
					for (int y = 1; y < srcImage.getHeight() - 1; y++) {
						if (maskImage.getRGB(x, y) != MASK_BACKGROUND) {
							// Move to the corresponding position in the destination image
							xDest = x + destPosition.x;
							yDest = y + destPosition.y;
							
							// Build the seamlessly cloned pixel
							rgb = ((int) Math.round(solutionsVectorRed.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.RED.shift() |
							      ((int) Math.round(solutionsVectorGreen.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.GREEN.shift() |
							      ((int) Math.round(solutionsVectorBlue.get(destToSolutionsMap.get(wDest * yDest + xDest)))) << ColorChannel.BLUE.shift() |
							      OPAQUE_BACKGROUND;
							
							finalImage.setRGB(xDest, yDest, rgb);
						}
					}
				}
				
				return finalImage;
		    }
		    catch(IterativeSolverNotConvergedException e) {
		    	throw new ComputationException("The iterative matrix solver could not converge to the solution.");
		    }
		}
		else {
			throw new ComputationException("One or more of the input images (either source, mask or destination) do not meet the requirements.");
		}
	}
	
	//TODO: Refactor as protected (after JUnit)
	public Map<Integer, Integer> createSolutionsMap() {
		int N = 0;
		ConcurrentHashMap<Integer, Integer> destToSolutionsMap = new ConcurrentHashMap<Integer, Integer>();
		for (int x = 1; x < srcImage.getWidth() - 1; x++) {
			for (int y = 1; y < srcImage.getHeight() - 1; y++) {
				if (maskImage.getRGB(x, y) != MASK_BACKGROUND) {
					// Move to the corresponding position in the destination image
					destToSolutionsMap.put(destImage.getWidth() * (y + destPosition.y) + (x + destPosition.x), N);
					// On our way, we'll know the number of solutions to compute
					N++;
				}
			}
	    }
		return destToSolutionsMap;
	}
	
	public BufferedImage getDestinationImage() {
		return destImage;
	}
	
	public Point2D getDestinationPosition() {
		return destPosition;
	}
	
	public BufferedImage getMaskImage() {
		return maskImage;
	}
	
	public BufferedImage getSourceImage() {
		return srcImage;
	}
	
	/**
	 * @param destToSolutionsMap
	 * @param destDivergence
	 * @param channel
	 * @return
	 * @throws ComputationException
	 * @throws IterativeSolverNotConvergedException
	 */
	protected Vector solvePoissonEquationsForChannel(Map<Integer, Integer> destToSolutionsMap, BufferedImage destDivergence, ColorChannel channel) throws ComputationException, IterativeSolverNotConvergedException {
		int N = destToSolutionsMap.size();
		
		/*
	     * WARNING : This part of the algorithm is a bit tricky to understand, 
	     * mostly because of the nature of sparse matrices, we cannot create them
	     * interactively. Thus, we need to create temporary arrays to accumulate its
	     * actual content. This is the reason why we need to use ArrayList's.
	     */
	    
		// This array will be used to prepare the sparse matrix (initial size N)
		List<MatrixCell> matrixDataList = new ArrayList<MatrixCell>(N);
			    
		// Prepare the right hand side vector, that will contain the conditions
		Vector rhsVector = new DenseVector(N);
		
		int solutionRow = 0;
		int xDest, yDest;

		// For each pixel in the cloned image (source image)
		for(int x = 1; x < srcImage.getWidth() - 1; x++) {
			for(int y = 1; y < srcImage.getHeight() - 1; y++) {
				if(maskImage.getRGB(x, y) != MASK_BACKGROUND) {
					// Move to the corresponding position in the destination image
					xDest = x + destPosition.x;
					yDest = y + destPosition.y;
					
					// Add Poisson equation, as needed, for each of the four neighbors
					
					// Top neighbor
					addPoissonEquationToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, 0, -1, channel);
					// Left neighbor
					addPoissonEquationToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, -1, 0, channel);
					// Bottom neighbor
					addPoissonEquationToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, 0, +1, channel);
					// Right neighbor
					addPoissonEquationToMatrix(matrixDataList, rhsVector, solutionRow, destToSolutionsMap, x, y, xDest, yDest, +1, 0, channel);

					// Set the condition on the diagonal
					matrixDataList.add(new MatrixCell(solutionRow, solutionRow, 4));
					
					// Construct the guidance field
					rhsVector.add(solutionRow, (destDivergence.getRGB(x, y) & channel.mask()) >> channel.shift());

					// Increment to the next row
					solutionRow++;
				}
			}
		}
		
		// Something wrong happened
		if(solutionRow != N)
			throw new ComputationException(String.format("(solutionRow != N) --> (%d != %d) ", solutionRow, N));
		
		// Prepare three primitives int[] array that will be used to load the data
		int[] rowsArray = new int[matrixDataList.size()];
		int[] colsArray = new int[matrixDataList.size()];
		int[] valuesArray = new int[matrixDataList.size()];
		
		// For each non-zero cells in the sparse matrix, initialize the primitives arrays
		int i = 0;
		for(MatrixCell cell: matrixDataList) {
			rowsArray[i] = cell.row;
			colsArray[i] = cell.col;
			valuesArray[i] = cell.value;
			
			i++;
		}
		
		// Prepare a circular byte buffer that will contain the data in memory
		CircularByteBuffer rawMatrixByteBuffer = new CircularByteBuffer(CircularByteBuffer.INFINITE_SIZE);
		
		// Write the metadata and actual data
		MatrixVectorWriter matrixWriter = new MatrixVectorWriter(rawMatrixByteBuffer.getOutputStream());
		matrixWriter.printMatrixInfo(new MatrixInfo(true, MatrixInfo.MatrixField.Integer, MatrixInfo.MatrixSymmetry.General));
		matrixWriter.printMatrixSize(new MatrixSize(N, N, matrixDataList.size()));
		matrixWriter.printCoordinate(rowsArray, colsArray, valuesArray, 1);
		matrixWriter.close();
		
		// Prepare to read the raw data from the compressed format
		BufferedReader rawMatrixReader = new BufferedReader(new InputStreamReader(rawMatrixByteBuffer.getInputStream()));
		
		Vector solutionsVector = null;
		try {
			// Prepare a NxN sparse matrix, that will contain the system linear of equations
			Matrix A = new CompRowMatrix(new MatrixVectorReader(rawMatrixReader));
			
			// Prepare the solution vector, that will contain the value of each computed pixel
			solutionsVector = Matrices.random(N);
		    
		    // Run a Bi-Conjugate iterative solver to compute Ax = b
		    IterativeSolver solver = new BiCG(solutionsVector);
		    
		    // Limit the solver iterations by setting up a custom monitor
		    solver.setIterationMonitor(new MatrixSolverIterationMonitor(SOLVER_ITERATIONS));
		    //solver.getIterationMonitor().setIterationReporter(new OutputIterationReporter());
		    
		    // Start the solver
		    solver.solve(A, rhsVector, solutionsVector);
		} 
		catch (IOException e) {}
		
		return solutionsVector;
	}
	
	/**
	 * Validate validation points
	 * @return true: valid false: invalid
	 */
	public boolean validateDestinationPosition() {
		// Make sure that the specified destination offset fits
		// the solver requirements and is inside the destination image.
		if(destPosition == null || 
		   destPosition.x <= 0 ||
		   destPosition.y <= 0 ||
		   destPosition.x >= destImage.getWidth() -1 ||
		   destPosition.y >= destImage.getHeight() -1)
			return false;
		
		// Destination Point + Source image must not be taller than Destination Image
		if ((destPosition.x + srcImage.getWidth())  > destImage.getWidth() ||
			(destPosition.y + srcImage.getHeight()) > destImage.getHeight())
			return false;
		
		return true;
	}

	public boolean validateInputImages() {
		return validateSourceImageSize() && validateDestinationPosition() && validateMask();
	}
	
	/**
	 * Validate mask
	 * @return true: valid false: invalid
	 */
	public boolean validateMask() {
		if (maskImage == null || destImage == null)
			return false;
		
		// Make sure that the mask fits in the destination area
	    if(destImage.getWidth() < maskImage.getWidth() || 
	 	   destImage.getHeight() < maskImage.getHeight())
	    	return false;
	    
		// Input a source image and a mask of different size
	    if(srcImage.getWidth() != maskImage.getWidth() ||
	       srcImage.getHeight() != maskImage.getHeight())
	    	return false;
	    
		// Destination image must not have the mask pasted so that a mask value of 0 touches the destination image edges
	    // verify for non-zeros values on top and bottom edges 
	    for(int x=0; x<maskImage.getWidth()-1; x++) {
	    	// Alpha 255 Red 0 Green 0 Blue 0 => 0xFF000000
	    	if (maskImage.getRGB(x, 0) != MASK_BACKGROUND ||
	    		maskImage.getRGB(x, maskImage.getHeight()-1) != MASK_BACKGROUND)
	    		return false;
	    }
	    	
	    // verify for non-zeros values on left and right edges 
    	for(int y=0; y<maskImage.getHeight()-1; y++) {
	    	// Alpha 255 Red 0 Green 0 Blue 0 => 0xFF000000
    		if (maskImage.getRGB(0, y) != 0xFF000000 ||
		    	maskImage.getRGB(maskImage.getWidth()-1, y) != MASK_BACKGROUND)
		    		return false;
    	}
	    
	    return true;
	}
	
	/**
	 * Validate Source Image correctness
	 * @return true: valid false: invalid
	 */
	public boolean validateSourceImageSize() {
		
		if (srcImage == null)
			return false;

		// Source must be smaller or equal than the destination image
		if (srcImage.getWidth() > destImage.getWidth() ||
			srcImage.getHeight() > destImage.getHeight()) {
			return false;
		}
		
		return true;
	}
}
	