package ca.etsmtl.photomontage.exceptions;

/**
 * @author fproulx
 *
 */
public class InvalidSourceImageSizeException extends Exception {
	private static final long serialVersionUID = -5658173095640966297L;
	
	/**
	 * @param msg
	 */
	public InvalidSourceImageSizeException(String msg) {
		super(msg);
	}
}
