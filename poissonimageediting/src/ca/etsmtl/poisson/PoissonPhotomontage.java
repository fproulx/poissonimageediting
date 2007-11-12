package ca.etsmtl.poisson;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.concurrent.ConcurrentHashMap;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.AbstractIterationMonitor;
import no.uib.cipr.matrix.sparse.BiCG;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.sparse.OutputIterationReporter;
import ca.etsmtl.poisson.exceptions.ComputationException;
import ca.etsmtl.poisson.exceptions.MaskTooLargeException;

/**
 * 
 * @author fproulx
 *
 */
public class PoissonPhotomontage {
	BufferedImage srcImage, maskImage, destImage;
	Point destPosition;
	private static final int ITERATIONS = 300;
	
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
		if(srcImage == null || destImage == null || maskImage == null)
			return false;
		
		//TODO < should be <>
		// Make sure that the mask fits in the destination area
	    if(srcImage.getWidth() < maskImage.getWidth() || 
	       srcImage.getHeight() < maskImage.getHeight() || 
	       destImage.getWidth() < maskImage.getWidth() || 
	       destImage.getHeight() < maskImage.getHeight())
	    	return false;
	    
		return true;
	}
	
	/**
	 * Validate Source Image correctness
	 * @return
	 */
	public boolean validateSourceImageSize() {

		// Source must be smaller or equal than the destination image
		if (srcImage.getWidth() <= destImage.getWidth() &&
			srcImage.getHeight() <= destImage.getHeight()) {
			return true;
		}
		return false;
	}
	
	public boolean validateDestinationPosition() {
		
		// Make sure that the specified destination offset fits
		// the solver requirements and is inside the destination image.
		if(destPosition != null && 
		   destPosition.x >= 0 &&
		   destPosition.y >= 0 &&
		   destPosition.x <= destImage.getWidth() -1 &&
		   destPosition.y <= destImage.getHeight() -1) {
		
			// Destination Point + Source image must not be taller than Destination Image
			if ((destPosition.x + srcImage.getWidth())  <= destImage.getWidth() &&
				(destPosition.y + srcImage.getHeight()) <= destImage.getHeight())
				return true;
		}
		
		return false;
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
		    int hDest = destImage.getHeight();
		    
		    int wMask = maskImage.getWidth();
		    int hMask = maskImage.getHeight();
		    
		    // Build a mapping between points in the destination image and the computed solutions 
		    int N = 0;
		    ConcurrentHashMap<Integer, Integer> destToSolutionsMap = new ConcurrentHashMap<Integer, Integer>();
		    for (int x = destPosition.x; x < destPosition.x + wMask; x++) {
				for (int y = destPosition.y; y < destPosition.y + hMask; y++) {
					// For each masked pixels over the offset position
					if(maskImage.getRGB(x - destPosition.x, y - destPosition.y) != 0) {
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
		    BufferedImage destDivergence = new BufferedImage(destImage.getWidth(), destImage.getHeight(), destImage.getType());
		    laplacianConv.filter(destImage, destDivergence);
		    
		    //TODO: Continue.
		    new MatrixVectorWriter();
		    // Compute --> Ax = b
		    
		    
		    // Prepare the right hand side vector, that will contain the conditions
		    Vector b = new DenseVector(N);
		    	    
		    int solutionRow = 0;
		    // For each pixel in the cloned image (source image)
		    for(int x = 0; x < wSrc; x++) {
		    	for(int y = 0; y < hSrc; y++) {
		    		if(maskImage.getRGB(x, y) != 0) {
		    			/*
		    			 * % the corresponding position in the destination image
				            yd = y - yoff;
				            xd = x - xoff; 
		    			 */
		    			
		    			// Check the neighboring pixels
		    			/*
		    			if imMask(y-1, x) ~= 0
			                % this pixel is already used
			                % get the diagonal position of the pixel
			                colIndex = imIndex(yd-1, xd);
			                M(count, colIndex) = -1;
			            else % at the top boundary
			                b(count) = b(count) + imDest(yd-1, xd);
			            end
			            
			            % if on the left
			            if imMask(y, x-1) ~= 0
			                colIndex = imIndex(yd, xd-1);
			                M(count, colIndex) = -1;
			            else % at the left boundary
			                b(count) = b(count) + imDest(yd, xd-1);
			            end            
			            
			            %------------------------------------------------------
			            % now the harder case, since this is not allocated
			            %------------------------------------------------------ 
			            % if on the bottom            
			            if imMask(y+1, x) ~= 0
			                colIndex = imIndex(yd+1, xd);
			                M(count, colIndex) = -1;
			            else    % at the bottom boundary
			                b(count) = b(count) + imDest(yd+1, xd);
			            end
			            
			            % if on the right side
			            if imMask(y, x+1) ~= 0
			                colIndex = imIndex(yd, xd+1);
			                M(count, colIndex) = -1;
			            else    % at the right boundary
			                b(count) = b(count) + imDest(yd, xd+1);
			            end       
			            
			            M(count, count) = 4;
			            
			            % construct the guidance field	
			            v = imLaplacian(y, x);
				
			            b(count) = b(count)+v;
		    			*/
		    			solutionRow++;
		    		}
		    	}
		    }
		    
	    	// WTF !?
		    if(solutionRow != N)
		    	throw new ComputationException();
		    
		    // Prepare a NxN sparse matrix, that will contain the system linear of equations 
		    Matrix A = new CompRowMatrix(new MatrixVectorReader(new BufferedReader()));
		    	 
		    // Prepare the solution vector, that will contain the value of each computed pixel
		    Vector x = Matrices.random(N);
		    
		    IterativeSolver solver = new BiCG(x);
		    
		    // Limit the solver iterations by setting up a custom monitor
		    solver.setIterationMonitor(new SimpleIterationMonitor(ITERATIONS));
		    solver.getIterationMonitor().setIterationReporter(new OutputIterationReporter());
		    
		    // Start the solver
		    long t0 = System.currentTimeMillis();
		    solver.solve(A, b, x);
		    long t1 = System.currentTimeMillis();

		    double itps = ITERATIONS / ((t1-t0)/1000.);

		    System.out.println("Iterations per second:\t" + itps);
		    
		    /*
			% reshape x to become the pixel intensity of the region
			% imRegion = reshape(x, widthRegion, heightRegion);
			
			%---------------------------------------------
			% now fill in the solved values
			%---------------------------------------------
			imNew = imDest;
			
			fprintf('\nRetriving result, filling destination image\n');
			tic
			% now fill in the 
			for y1 = 1:heightDest
			    for x1 = 1:widthDest
			        if imMask(y1+yoff, x1+xoff) ~= 0
			            index = imIndex(y1, x1);
			            imNew(y1, x1) = x(index);
			        end
			    end
			end
		     */
			return null;
		}
		else
			return null;
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
	