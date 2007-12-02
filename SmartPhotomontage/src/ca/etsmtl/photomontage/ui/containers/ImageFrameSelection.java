package ca.etsmtl.photomontage.ui.containers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the state of the selection and the points that makes this selection  
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel <lebel.kim@gmail.com>, Jean-Philippe Plante <jphilippeplante@gmail.com>, Francois Proulx <francois.proulx@gmail.com> 
 * TODO improve this class's friendliness with ImageFrameMouseListener 
 */
public class ImageFrameSelection {

	private boolean mode = true;
	private List<Point> points = new ArrayList<Point>();
	
	public boolean isSelectionMode() {
		return mode;
	}
	
	public synchronized void setMode(boolean mode) {
		this.mode = mode;
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public synchronized void setPoints(List<Point> points) {
		this.points = points;
	}
}
