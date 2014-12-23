package com.link_intersystems.swing;

import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RowSorterAwareListSelectionModelUpdater {

	private ListSelectionModelSync listSelectionModelSync = new ListSelectionModelSync();
	private ListSelectionModel targetModel;
	private ListSelectionModel sourceModel;
	private RowSorter<?> rowSorter;

	public RowSorterAwareListSelectionModelUpdater(
			ListSelectionModel targetModel) {
		this.targetModel = targetModel;
	}

	public void setSourceModel(ListSelectionModel listSelectionModel,
			RowSorter<?> rowSorter) {
		if (this.sourceModel != null) {
			this.sourceModel
					.removeListSelectionListener(listSelectionModelSync);
		}
		this.rowSorter = rowSorter;
		this.sourceModel = listSelectionModel;
		if (this.sourceModel != null) {
			this.sourceModel.addListSelectionListener(listSelectionModelSync);
		}

	}

	private void updateTargetModel() {
		int minIndex = sourceModel.getMinSelectionIndex();
		int maxIndex = sourceModel.getMaxSelectionIndex();

		targetModel.setValueIsAdjusting(true);
		targetModel.clearSelection();
		for (int index = minIndex; index <= maxIndex; index++) {
			if (sourceModel.isSelectedIndex(index)) {
				int targetIndex = index;
				if (rowSorter != null) {
					targetIndex = rowSorter.convertRowIndexToModel(index);
				}
				targetModel.addSelectionInterval(targetIndex, targetIndex);
			}
		}
		targetModel.setValueIsAdjusting(false);
	}

	private class ListSelectionModelSync implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				updateTargetModel();
			}
		}

	}

}
