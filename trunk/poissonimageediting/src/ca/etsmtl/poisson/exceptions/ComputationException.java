package ca.etsmtl.poisson.exceptions;

public class ComputationException extends Exception {
	private static final long serialVersionUID = -1982069004778632902L;

	public ComputationException() {
	}
	
	public ComputationException(String msg) {
		super("The computation could not complete successfully because of: " + msg);
	}
}
