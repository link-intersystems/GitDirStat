package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

public class InvertSelectionAction extends AbstractAction {

	private static final long serialVersionUID = -3842791426045430962L;
	private ListSelectionModel listSelectionModel;
	private ListModel listModel;

	public InvertSelectionAction(ListSelectionModel listSelectionModel,
			ListModel listModel) {
		this.listSelectionModel = listSelectionModel;
		this.listModel = listModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int size = listModel.getSize();
		listSelectionModel.setValueIsAdjusting(true);
		ListSelectionModelMemento listSelectionModelMemento = new ListSelectionModelMemento();
		listSelectionModelMemento.save(listSelectionModel);
		DefaultListSelectionModel oldSelection = new DefaultListSelectionModel();
		listSelectionModelMemento.restore(oldSelection);

		listSelectionModel.clearSelection();
		for (int i = 0; i < size; i++) {
			if (!oldSelection.isSelectedIndex(i)) {
				listSelectionModel.addSelectionInterval(i, i);
			}
		}
		listSelectionModel.setValueIsAdjusting(false);
	}
}
