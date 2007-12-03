package ca.etsmtl.photomontage.exceptions;

/**
 * @author fproulx
 *
 */
public class InvalidDestinationPositionException extends Exception {
	private static final long serialVersionUID = -3245041740687585000L;
	
	/**
	 * @param msg
	 */
	public InvalidDestinationPositionException(String msg) {
		super(msg);
	}
}
