package ca.etsmtl.photomontage.exceptions;

public class InvalidSourceImageSizeException extends Exception {
	private static final long serialVersionUID = -5658173095640966297L;
	private String reason;
	
	public InvalidSourceImageSizeException(String reason) {
		this.reason = reason;
	}
	
	public String getMessage() {
		return "";
	}
	
	public String getReason() {
		return reason;
	}
}
