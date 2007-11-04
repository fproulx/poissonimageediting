package ui;

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

/*
 * ImageBrowser.java
 *
 * Created on May 3, 2007, 3:11 PM
 *
 * Copyright (c) 2007, Sun Microsystems, Inc
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * Based on a demo of the AnimatedTransitions library uses a layout manager to
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
	 * Add a image in the image browser panel
	 * 
	 * @param image
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