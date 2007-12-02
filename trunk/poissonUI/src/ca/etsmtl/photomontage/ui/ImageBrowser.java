package ca.etsmtl.photomontage.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jdesktop.tools.io.FileTreeWalk;
import org.jdesktop.tools.io.FileTreeWalker;
import org.jdesktop.tools.io.UnixGlobFileFilter;

import ca.etsmtl.photomontage.ui.containers.ImageFramesContainer;
import ca.etsmtl.photomontage.ui.containers.ImageHolder;
import ca.etsmtl.photomontage.ui.containers.PreviewContainer;
import ca.etsmtl.photomontage.ui.events.ImageBrowserMouseListener;


/**
 * Image Browser based on a demo of the AnimatedTransitions library uses a layout manager to
 * assist in setting up the next screen that the application transitions to.
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class ImageBrowser extends JComponent {

	/**
	 * generated serial uid
	 */
	private static final long serialVersionUID = 2327906987907620984L;

	Dimension newSize = new Dimension();

	public static List<ImageHolder> images = new ArrayList<ImageHolder>();

	public static int currentSize = 175;

	private ImageFramesContainer container;

	private PreviewContainer preview;

	/** Creates a new instance of ImageBrowser */
	public ImageBrowser(ImageFramesContainer container, PreviewContainer preview) {
		this.container = container;
		this.preview = preview;

		setOpaque(true);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		loadImages();

		// For each image:
		// - set the icon at the current thumbnail size
		// - create/set a custom effect that will move/scale the
		// images. Note that the main reason for the custom effect
		// is that scaling effects typically redraw the actual component
		// instead of using image tricks. In this case, image tricks are
		// just fine. So the custom effect is purely an optimization here.
		for (int i = 0; i < images.size(); ++i) {
			JLabel label = new JLabel();
			label.setIcon(new ImageIcon(images.get(i).getImage(currentSize)));

			// add listener for select image
			label.addMouseListener(new ImageBrowserMouseListener(images.get(i),
					container, preview));

			add(label);
		}
	}

	/**
	 * Loads all images found in the directory "images" (which therefore must be
	 * found in the folder in which this app runs).
	 */
	private void loadImages() {
		try {
			File imagesDir = new File("images");
			FileTreeWalker walker = new FileTreeWalker(imagesDir,
					new UnixGlobFileFilter("*.jpg"));
			walker.walk(new FileTreeWalk() {

				public void walk(File path) {
					try {
						BufferedImage image = ImageIO.read(path);
						images.add(new ImageHolder(image, path.getCanonicalPath()));
					} catch (Exception e) {
						System.out.println("Problem loading images: " + e);
					}
				}
			});
		} catch (Exception e) {
			System.out.println("Problem loading images: " + e);
		}
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
		label.addMouseListener(new ImageBrowserMouseListener(holder, container,
				preview));

		add(label);

		revalidate();
	}
}