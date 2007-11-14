package ca.etsmtl.poisson;


import java.awt.image.BufferedImage;

/**
 * 
 * @author fproulx
 *
 */
public class ImageConversionUtilities {
	
	public static FloatPixel[][] createFloatPixelArrayFromImage(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		FloatPixel[][] pixels = new FloatPixel[w][h];
		
		// Extract pixels from the BufferedImage
		int[] rgb = img.getRGB(0, 0, w, h, null, 0, w);
		for(int x=0; x < w; x++) {
			for(int y=0; y < h; y++) {
				// Extract channels from the pixel
				int r = (rgb[(y*w)+x] & 0x00FF0000); 
				int g = (rgb[(y*w)+x] & 0x0000FF00); 
				int b = (rgb[(y*w)+x] & 0x000000FF);
				// Scale each channel over a floating point value (0.0f to 1.0f)
				pixels[x][y] = new FloatPixel(r / 255.0f, g / 255.0f, b / 255.0f);
			}
		}
		
		return pixels;
	}
}
