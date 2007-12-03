package ca.etsmtl.photomontage.exceptions;

public class InvalidMaskException extends Exception {
	private static final long serialVersionUID = 819910481660041482L;
	private String reason;
	
	public InvalidMaskException(String reason) {
		this.reason = reason;
	}
	
	public String getMessage() {
		return "";
	}
	
	public String getReason() {
		return reason;
	}
}
