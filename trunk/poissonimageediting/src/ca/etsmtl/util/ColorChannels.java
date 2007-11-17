package ca.etsmtl.util;

public enum ColorChannels {
	 RED (0x00FF0000, 16),
	 GREEN (0x0000FF00, 8),
	 BLUE (0x000000FF, 0),
	 ALPHA (0xFF000000, 24);
	 
	 private final int mask, shift;
	 ColorChannels(int mask, int shift) {
		 this.mask = mask;
		 this.shift = shift;
	 }
	 
	 public int mask() { return mask; }
	 public int shift() { return shift; }
}