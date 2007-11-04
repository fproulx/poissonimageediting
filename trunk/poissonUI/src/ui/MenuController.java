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
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
				browser.addImage(image, file.getCanonicalPath());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
				    "Error while open the image.",
				    "Error while opening",
				    JOptionPane.ERROR_MESSAGE);
		}

		return image;
	}

	public void saveFile(BufferedImage image) {
		//create a new file chooser
		String filename = File.separator + "Users";
		JFileChooser fc = new JFileChooser(filename);
		
		//change dialog type to save
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Untitled");

		
		int returnVal = fc.showSaveDialog(null);

		try {
			//if ok save the new image
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File myfile = fc.getSelectedFile();
				ImageIO.write(image, "jpg", myfile);
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(null,
				    "Error while saving the new image.",
				    "Error while saving",
				    JOptionPane.ERROR_MESSAGE);
		}

	}
}