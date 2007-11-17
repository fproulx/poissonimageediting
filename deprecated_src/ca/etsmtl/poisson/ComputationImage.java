package ca.etsmtl.poisson;


public class ComputationImage<T extends Pixel<?>> {
	T[][] pixels;
	int width, height;
	
	@SuppressWarnings("unchecked")
	public ComputationImage(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public T[][] getPixels() {
		return pixels;
	}

	public void setPixels(T[][] pixels) {
		this.pixels = pixels;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
