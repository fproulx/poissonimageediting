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
import java.util.concurrent.ConcurrentHashMap;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.io.MatrixInfo;
import no.uib.cipr.matrix.io.MatrixSize;
import no.uib.cipr.matrix.io.MatrixVectorReader;
import no.uib.cipr.matrix.io.MatrixVectorWriter;
import no.uib.cipr.matrix.sparse.AbstractIterationMonitor;
import no.uib.cipr.matrix.sparse.BiCG;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.sparse.OutputIterationReporter;
import ca.etsmtl.poisson.exceptions.ComputationException;

import com.Ostermiller.util.CircularByteBuffer;

/**
 * 
 * @author fproulx
 *
 */
public class PoissonPhotomontage {
	BufferedImage srcImage, maskImage, destImage;
	Point destPosition;
	private static final int ITERATIONS = 300;
	private static final int MASK_BACKGROUND = 0xFF000000;
	
	public PoissonPhotomontage(BufferedImage srcImage, BufferedImage maskImage, BufferedImage destImage, Point destPosition) {
		setSourceImage(srcImage);
		setMaskImage(maskImage);
		setDestinationImage(destImage);
		setDestinationPosition(destPosition);
	}

	public BufferedImage getSourceImage() {
		return srcImage;
	}

	public void setSourceImage(BufferedImage srcImage) {
		this.srcImage = srcImage;
	}

	public BufferedImage getMaskImage() {
		return maskImage;
	}

	public void setMaskImage(BufferedImage maskImage) {
		this.maskImage = maskImage;
	}

	public BufferedImage getDestinationImage() {
		return destImage;
	}

	public void setDestinationImage(BufferedImage destImage) {
		this.destImage = destImage;
	}

	public Point2D getDestinationPosition() {
		return destPosition;
	}

	public void setDestinationPosition(Point destPosition) {
		this.destPosition = destPosition;
	}
	
	public boolean validateInputImages() {
		
		boolean isValid;
		
		isValid = validateSourceImageSize();
		isValid = validateDestinationPosition();
		isValid = validateMask();
		
		return isValid;
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
	
	/**
	 * Validate validation points
	 * @return true: valid false: invalid
	 */
	public boolean validateDestinationPosition() {
		
		if (destPosition == null)
			return false;
		
		// Make sure that the specified destination offset fits
		// the solver requirements and is inside the destination image.
		if(destPosition == null || 
		   destPosition.x >= 0 ||
		   destPosition.y >= 0 ||
		   destPosition.x <= destImage.getWidth() -1 ||
		   destPosition.y <= destImage.getHeight() -1)
			return false;
		
		// Destination Point + Source image must not be taller than Destination Image
		if ((destPosition.x + srcImage.getWidth())  > destImage.getWidth() ||
			(destPosition.y + srcImage.getHeight()) > destImage.getHeight())
			return false;
		
		return true;
	}
	
	public ComputationImage<VectorPixel> computeGradientVectorField(ComputationImage<FloatPixel> img) {
		int w = img.getWidth();
		int h = img.getHeight();
		ComputationImage<VectorPixel> gradientVectorField = new ComputationImage<VectorPixel>(w, h);
		VectorPixel[][] tmpVectorField = new VectorPixel[w][h];
		FloatPixel[][] pixels = img.getPixels();
		// N.B : Notice x = 0 --> x < w-1
		for (int x = 0; x < w-1; x++) {
			for (int y = 0; y < h-1; y++) {
				// For each pixel in the provided image, compute the gradient (u,v) for each channel (RGB)
				tmpVectorField[x][y] = new VectorPixel(new Vector2f(pixels[x+1][y].r - pixels[x][y].r, pixels[x][y+1].r - pixels[x][y].r),
						                               new Vector2f(pixels[x+1][y].g - pixels[x][y].g, pixels[x][y+1].g - pixels[x][y].g),
						                               new Vector2f(pixels[x+1][y].b - pixels[x][y].b, pixels[x][y+1].b - pixels[x][y].b));
			}
		}
		gradientVectorField.setPixels(tmpVectorField);
		// Return the computed gradient vector field
		return gradientVectorField;
	}
	
	public ComputationImage<FloatPixel> computeDivergenceScalarField(ComputationImage<VectorPixel> img) {
		int w = img.getWidth();
		int h = img.getHeight();
		ComputationImage<FloatPixel> divergenceScalarField = new ComputationImage<FloatPixel>(w, h);
		FloatPixel[][] tmpScalarField = new FloatPixel[w][h];
		VectorPixel[][] vectors = img.getPixels();
		// N.B : Notice x = 1 --> x < w
		for (int x = 1; x < w; x++) {
			for (int y = 1; y < h; y++) {
				// For each vector in the provided image, compute the divergent of each channel (RGB)
				tmpScalarField[x][y] = new FloatPixel((vectors[x][y].r.u - vectors[x-1][y].r.u) + (vectors[x][y].r.v - vectors[x][y-1].r.v),
						                              (vectors[x][y].g.u - vectors[x-1][y].g.u) + (vectors[x][y].g.v - vectors[x][y-1].g.v),
						                              (vectors[x][y].b.u - vectors[x-1][y].b.u) + (vectors[x][y].b.v - vectors[x][y-1].b.v));
			}
		}
		divergenceScalarField.setPixels(tmpScalarField);
		// Return the computed gradient vector field
		return divergenceScalarField;
	}
	
	public BufferedImage createPhotomontage() throws ComputationException, IterativeSolverNotConvergedException {
		// Make sure the input images fit the requirements
		if(validateInputImages()) {
			int wSrc = srcImage.getWidth();
		    int hSrc = srcImage.getHeight();
		    
		    int wDest = destImage.getWidth();
		    
		    int wMask = maskImage.getWidth();
		    int hMask = maskImage.getHeight();
		    
		    int xOffset = destPosition.x;
		    int yOffset = destPosition.y;
		    
		    // Build a mapping between points in the destination image and the computed solutions 
		    int N = 0;
		    ConcurrentHashMap<Integer, Integer> destToSolutionsMap = new ConcurrentHashMap<Integer, Integer>();
		    for (int x = destPosition.x; x < destPosition.x + wMask; x++) {
				for (int y = destPosition.y; y < destPosition.y + hMask; y++) {
					// For each masked pixels over the offset position
					if(maskImage.getRGB(x - destPosition.x, y - destPosition.y) != MASK_BACKGROUND) {
						destToSolutionsMap.put(wDest * y + x, N);
						// On our way, we'll know the number of solutions to compute
						N++;
					}
				}
		    }
		    
		    // Prepare a 3x3 Laplacian kernel for 2D convolution
		    Kernel laplacian = new Kernel(3, 3, 
		    		                      new float[] { 0, -1,  0,
		    		                                   -1,  4, -1,
		    		                                    0, -1,  0});
		    // Prepare a 2D Laplacian convolution, don't compute the edges
		    ConvolveOp laplacianConv = new ConvolveOp(laplacian, ConvolveOp.EDGE_NO_OP, null);
		    // Compute the divergence of the destination image (i.e. by applying the Laplacian kernel)
		    BufferedImage destDivergence = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		    laplacianConv.filter(destImage, destDivergence);
		    
		    /*
		     * WARNING : This part of the algorithm is a bit tricky to understand, 
		     * mostly because of the nature of sparse matrices, we cannot create them
		     * interactively. Thus, we need to create temporary arrays to accumulate its
		     * actual content. This is the reason why we need to use ArrayList's.
		     */
		    
		    // This array will be used to prepare the sparse matrix (initial size N)
		    ArrayList<MatrixCell> matrixDataList = new ArrayList<MatrixCell>(N);
		    	    
		    // Prepare the right hand side vector, that will contain the conditions
		    Vector b = new DenseVector(N);
		    
		    int solutionRow = 0;
		    // For each pixel in the cloned image (source image)
		    for(int x = 1; x < wSrc - 1; x++) {
		    	for(int y = 1; y < hSrc - 1; y++) {
		    		if(maskImage.getRGB(x, y) != MASK_BACKGROUND) {
		    			// Move to the corresponding position in the destination image
		    			int xd = x + xOffset;
		    			int yd = y + yOffset;
		    			
		    			// Check the neighboring pixels
		    			
		    			// If the pixel ABOVE is also part of the mask
		    			if(maskImage.getRGB(x, y-1) != MASK_BACKGROUND) {
		    				// This pixel is already used, get the diagonal position of the pixel
		    				matrixDataList.add(new MatrixCell(solutionRow, destToSolutionsMap.get(wDest * (yd - 1) + xd), -1));
		    			}
		    			else { // TOP boundary
		    				// b[solutionRow] += value
		    				//TODO: WARNING RED VALUE ONLY FOR NOW !
		    				b.add(solutionRow, (destImage.getRGB(xd, yd - 1) & 0x00FF0000) >> 16);
		    			}
		    			
		    			// If the pixel ON THE LEFT is also part of the mask
		    			if(maskImage.getRGB(x - 1, y) != MASK_BACKGROUND) {
		    				// This pixel is already used, get the diagonal position of the pixel
		    				matrixDataList.add(new MatrixCell(solutionRow, destToSolutionsMap.get(wDest * yd + (xd - 1)), -1));
		    			}
		    			else { // LEFT boundary
		    				// b[solutionRow] += value
		    				//TODO: WARNING RED VALUE ONLY FOR NOW !
		    				b.add(solutionRow, (destImage.getRGB(xd - 1, yd) & 0x00FF0000) >> 16);
		    			}
		    			
		    			// If the pixel AT THE BOTTOM is also part of the mask
		    			if(maskImage.getRGB(x, y + 1) != MASK_BACKGROUND) {
		    				// This pixel is already used, get the diagonal position of the pixel
		    				matrixDataList.add(new MatrixCell(solutionRow, destToSolutionsMap.get(wDest * (yd + 1) + xd), -1));
		    			}
		    			else { // BOTTOM boundary
		    				// b[solutionRow] += value
		    				//TODO: WARNING RED VALUE ONLY FOR NOW !
		    				b.add(solutionRow, (destImage.getRGB(x, yd + 1) & 0x00FF0000) >> 16);
		    			}
		    			
		    			// If the pixel ON THE RIGHT is also part of the mask
		    			if(maskImage.getRGB(x + 1, y) != MASK_BACKGROUND) {
		    				// This pixel is already used, get the diagonal position of the pixel
		    				matrixDataList.add(new MatrixCell(solutionRow, destToSolutionsMap.get(wDest * yd + (xd + 1)), -1));
		    			}
		    			else { // RIGHT boundary
		    				// b[solutionRow] += value
		    				//TODO: WARNING RED VALUE ONLY FOR NOW !
		    				b.add(solutionRow, (destImage.getRGB(xd + 1, yd) & 0x00FF0000) >> 16);
		    			}

		    			// Set the condition on the diagonal
	    				matrixDataList.add(new MatrixCell(solutionRow, solutionRow, 4));
	    				
	    				// Construct the guidance field
	    				b.add(solutionRow, (destDivergence.getRGB(x, y) & 0x00FF0000) >> 16);

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
		    
			try {
				// Prepare a NxN sparse matrix, that will contain the system linear of equations
				Matrix A = new CompRowMatrix(new MatrixVectorReader(rawMatrixReader));
				
				// Prepare the solution vector, that will contain the value of each computed pixel
			    Vector solutionsVector = Matrices.random(N);
			    
			    // Run a Bi-Conjugate iterative solver to compute Ax = b
			    IterativeSolver solver = new BiCG(solutionsVector);
			    
			    // Limit the solver iterations by setting up a custom monitor
			    solver.setIterationMonitor(new SimpleIterationMonitor(ITERATIONS));
			    solver.getIterationMonitor().setIterationReporter(new OutputIterationReporter());
			    
			    // Start the solver
			    long t0 = System.currentTimeMillis();
			    solver.solve(A, b, solutionsVector);
			    long t1 = System.currentTimeMillis();

			    double itps = ITERATIONS / ((t1-t0)/1000.);

			    System.out.println("Iterations per second:\t" + itps);

				// Copy the destination into the montage (the background)
				BufferedImage finalImage = new BufferedImage(destImage.getWidth(), destImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D) finalImage.getGraphics();
				g2d.drawImage(destImage, 0, 0, null);

				// For each pixel in the cloned image (source image)
				for (int x = 1; x < wSrc - 1; x++) {
					for (int y = 1; y < hSrc - 1; y++) {
						if (maskImage.getRGB(x, y) != MASK_BACKGROUND) {
							// Move to the corresponding position in the
							// destination image
							int xDest = x + xOffset;
							int yDest = y + yOffset;
							
							finalImage.setRGB(xDest, yDest, (int) Math.round(solutionsVector.get(destToSolutionsMap.get(wDest * yDest + xDest))));
						}
					}
				}

				return finalImage;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static class MatrixCell {
		public final int row;
		public final int col;
		public final int value;
		
		public MatrixCell(final int row, final int col, final int value) {
			this.row = row;
			this.col = col;
			this.value = value;
		}
	}
	
	private static class SimpleIterationMonitor extends AbstractIterationMonitor {

		private int max;

		public SimpleIterationMonitor(int max) {
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
	