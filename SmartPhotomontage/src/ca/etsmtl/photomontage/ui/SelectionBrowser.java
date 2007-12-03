package ca.etsmtl.photomontage.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;


import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.SelectionHolder;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseListener;
import ca.etsmtl.photomontage.ui.events.ImageFrameMouseMotionListener;
import ca.etsmtl.photomontage.ui.events.SelectionBrowserMouseListener;

import com.developpez.gfx.swing.drag.GhostGlassPane;
import com.developpez.gfx.swing.drag.GhostMotionAdapter;


/**
 * 
 * Selection Browser based on ImageBrowser
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class SelectionBrowser extends JComponent {
	private static final long serialVersionUID = 8651024289475405132L;

	Dimension newSize = new Dimension();

	public static List<ImageHolder> images = new ArrayList<ImageHolder>();

	public final static int currentSize = 180;
	
	/** Creates a new instance of ImageBrowser */
	public SelectionBrowser() {

		initComponents();
	}
	
	private void initComponents() {
		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setPreferredSize(new Dimension(currentSize, 0));
	}

	/**
	 * Ajoute une image dans le image browser
	 * 
	 * @param image est l'image en bufferedimage � ajouter
	 * @param filename est le path de l'image
	 */
	public void addImage(SelectionHolder selHold) {

		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(selHold.getScaledImage()));

		GhostGlassPane glassPane = (GhostGlassPane) UIApp.getApplication().getMainFrame().getGlassPane();
		
		SelectionBrowserMouseListener mouseListener = new SelectionBrowserMouseListener(glassPane, selHold);
		label.addMouseListener(mouseListener);
		
		GhostMotionAdapter mouseMotionListener = new GhostMotionAdapter(glassPane);
		label.addMouseMotionListener(mouseMotionListener);
		
		add(label);
		revalidate();
	}
}