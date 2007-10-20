package ca.etsmtl.poisson;

public abstract class Pixel<T> {
	final public T r, g, b;
	
	public Pixel(T r, T g, T b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
