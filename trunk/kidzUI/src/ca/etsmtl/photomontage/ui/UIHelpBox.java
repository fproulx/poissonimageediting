/*
 * SmartPhotomontage
 * Copyright (C) 2008
 * Olivier Bilodeau
 * http://poissonimageediting.googlecode.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package ca.etsmtl.photomontage.ui;

import org.jdesktop.application.Action;

/**
 * UIAboutBox est le about en UI
 *
 */
public class UIHelpBox extends javax.swing.JDialog {

	/**
	 * generated serial uid
	 */
	private static final long serialVersionUID = -5457021910108760127L;

	/**
	 * Constructor
	 * @param parent
	 */
	public UIHelpBox(java.awt.Frame parent) {
		super(parent);
		initComponents();
		getRootPane().setDefaultButton(closeButton);
	}

	/**
	 * close the about box
	 */
	@Action
	public void closeHelpBox() {
		setVisible(false);
	}


	private void initComponents() {

		closeButton = new javax.swing.JButton();
		javax.swing.JLabel imageLabel = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application
				.getInstance(ca.etsmtl.photomontage.ui.UIApp.class).getContext().getResourceMap(
						UIHelpBox.class);
		setTitle(resourceMap.getString("title")); // NOI18N
		setName("helpBox"); // NOI18N
		setResizable(false);

		javax.swing.ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(ca.etsmtl.photomontage.ui.UIApp.class).getContext().getActionMap(
						UIHelpBox.class, this);
		closeButton.setAction(actionMap.get("closeHelpBox")); // NOI18N
		closeButton.setText(resourceMap.getString("closeAboutBox.Action.text"));

		imageLabel.setIcon(resourceMap.getIcon("imageLabel.icon")); // NOI18N

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(imageLabel)
										.add(18, 18, 18)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING)
														.add(
																org.jdesktop.layout.GroupLayout.LEADING,
																layout
																		.createSequentialGroup()
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
														.add(closeButton)))));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(imageLabel,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(19, 19, Short.MAX_VALUE).add(
												closeButton).addContainerGap()));

		pack();

	}
	private javax.swing.JButton closeButton;


}
