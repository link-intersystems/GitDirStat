package com.link_intersystems.swing;

import java.util.BitSet;

import javax.swing.ListSelectionModel;

public class ListSelectionModelMemento {

	private int minIndex;
	private int maxIndex;
	private BitSet selectionMemento;

	public void save(ListSelectionModel listSelectionModel){
		minIndex = listSelectionModel.getMinSelectionIndex();
		maxIndex = listSelectionModel.getMaxSelectionIndex();
		selectionMemento = new BitSet((maxIndex - minIndex) + 1);

		for (int i = minIndex; i <= maxIndex; i++) {
			boolean isSelected = listSelectionModel.isSelectedIndex(i);
			int selectionMementoIndex = i - minIndex;
			if (isSelected) {
				selectionMemento.set(selectionMementoIndex);
			} else {
				selectionMemento.clear(selectionMementoIndex);
			}
		}
	}

	public void restore(ListSelectionModel listSelectionModel) {
		listSelectionModel.clearSelection();

		for (int i = minIndex; i <= maxIndex; i++) {
			int selectionMementoIndex = i - minIndex;
			boolean selected = selectionMemento.get(selectionMementoIndex);
			if (selected) {
				listSelectionModel.addSelectionInterval(i, i);
			}
		}

	}
}
