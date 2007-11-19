package ca.etsmtl.matrix;

public class MatrixCell {
	public final int row;
	public final int col;
	public final int value;
	
	public MatrixCell(final int row, final int col, final int value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}
}