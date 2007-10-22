package ca.etsmtl.poisson;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * 
 * @author fproulx
 *
 */
public class PoissonPhotomontage {
	BufferedImage srcImage, maskImage, destImage;
	Point destPosition;
	
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
	
	private boolean validateInputImages() {
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
	
	public BufferedImage createPhotomontage() {
		validateInputImages();
		
		//  See http://java.sun.com/docs/books/tutorial/2d/advanced/compositing.html
		BufferedImage buffer = new BufferedImage(10, 10, BufferedImage.TYPE_BYTE_BINARY); // Create a bitmask (0 or 255)
		Graphics2D g2 = buffer.createGraphics();
	    g2.setComposite(AlphaComposite.DstOut);
	    g2.drawImage(null, null, 0, 0);
	    g2.dispose();
	    
		return null;
	}

}
	