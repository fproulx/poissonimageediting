package ui.dnd;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

/**
 * Classe ImageSelectionDrop implémente les événements d'un droptarget listener
 * 
 * TODO implémentation drag-n-drop
 * 
 * @author Olivier Bilodeau <olivier.bilodeau.1@gmail.com>, Kim Lebel
 *         <lebel.kim@gmail.com>, Jean-Philippe Plante
 *         <jphilippeplante@gmail.com>, Francois Proulx
 *         <francois.proulx@gmail.com>
 * 
 */
public class ImageSelectionDrop implements DropTargetListener {

	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		System.out.println("dragEnter" + dtde.getSource());
	}

	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		System.out.println("dragExit" + dte.getSource());
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		System.out.println("dragOver" + dtde.getSource());
	}

	public void drop(DropTargetDropEvent dtde) {
		// TODO Auto-generated method stub
		System.out.println("drop" + dtde.getSource());
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		System.out.println("dropActionChanged" + dtde.getSource());
	}

}
