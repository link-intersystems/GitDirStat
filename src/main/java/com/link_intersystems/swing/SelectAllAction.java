package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class SelectAllAction extends AbstractAction {

	private static final long serialVersionUID = -3842791426045430962L;
	private ListSelectionModel listSelectionModel;
	private ListModel listModel;

	public SelectAllAction(ListSelectionModel listSelectionModel,
			ListModel listModel) {
		this.listSelectionModel = listSelectionModel;
		this.listModel = listModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int size = listModel.getSize();
		listSelectionModel.setValueIsAdjusting(true);
		listSelectionModel.clearSelection();
		if (size > 0) {
			listSelectionModel.addSelectionInterval(0, size - 1);
		}
		listSelectionModel.setValueIsAdjusting(false);
	}
}
