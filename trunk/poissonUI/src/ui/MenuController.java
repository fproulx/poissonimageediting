/*
 * MenuController.java
 *
 * Created on Oct 28, 2007, 11:51:58 AM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 */
public class MenuController {

	public BufferedImage openFile(ImageBrowser browser) {

		BufferedImage image = null;

		String filename = File.separator + "Users";
		JFileChooser fc = new JFileChooser(new File(filename));

		// Show open dialog; this method does not return until the dialog is
		// closed
		fc.showOpenDialog(null);

		try {
			File file = fc.getSelectedFile();
			if (file.exists() && file.canRead() && file.isFile()) {
				image = ImageIO.read(file);
				browser.addImage(image);
			}
		} catch (Exception e) {
			System.out.println("Problem loading image: " + e);
		}

		return image;
	}

	public void saveFile(BufferedImage image) {
	}
}