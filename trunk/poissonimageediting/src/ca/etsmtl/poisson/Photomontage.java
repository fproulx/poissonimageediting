package ca.etsmtl.poisson;

import java.awt.image.BufferedImage;

import ca.etsmtl.poisson.exceptions.ComputationException;

public interface Photomontage {
	public BufferedImage createPhotomontage() throws ComputationException;
}
