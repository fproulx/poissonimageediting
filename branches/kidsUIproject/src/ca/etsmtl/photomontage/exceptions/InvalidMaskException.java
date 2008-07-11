package ca.etsmtl.photomontage.exceptions;

/**
 * @author fproulx
 *
 */
public class InvalidMaskException extends Exception {
	private static final long serialVersionUID = 819910481660041482L;
	
	/**
	 * @param msg
	 */
	public InvalidMaskException(String msg) {
		super(msg);
	}
}
