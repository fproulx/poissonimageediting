package ca.etsmtl.photomontage.exceptions;

public class InvalidDestinationPositionException extends Exception {
	private static final long serialVersionUID = -3245041740687585000L;
	private String reason;
	
	public InvalidDestinationPositionException(String reason) {
		this.reason = reason;
	}
	
	public String getMessage() {
		return "";
	}
	
	public String getReason() {
		return reason;
	}
}
