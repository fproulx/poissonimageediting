package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import ui.containers.ImageHolder;


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

	public static int currentSize = 175;

	/** Creates a new instance of ImageBrowser */
	public SelectionBrowser() {

		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		//TODO: ici ajouter ghost drop lis de romain
	}

	/**
	 * Ajoute une image dans le image browser
	 * 
	 * @param image est l'image en bufferedimage à ajouter
	 * @param filename est le path de l'image
	 */
	public void addImage(BufferedImage image, String filename) {
		ImageHolder holder = new ImageHolder(image, filename);

		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(holder.getScaledImage()));

		// add listener for select image
		//TODO: romain guy rules !
		//label.addMouseListener(new ImageBrowserMouseListener(holder, container, preview));

		add(label);

		revalidate();
	}
}