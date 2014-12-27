package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ListSelectionModel;

public class UnselectAllAction extends AbstractAction {

	private static final long serialVersionUID = -3842791426045430962L;
	private ListSelectionModel listSelectionModel;

	public UnselectAllAction(ListSelectionModel listSelectionModel) {
		this.listSelectionModel = listSelectionModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		listSelectionModel.setValueIsAdjusting(true);
		listSelectionModel.clearSelection();
	}
}
