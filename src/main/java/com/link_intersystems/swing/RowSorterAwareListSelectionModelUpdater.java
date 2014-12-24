package com.link_intersystems.swing;

import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RowSorterAwareListSelectionModelUpdater {

	private ListSelectionModelSync targetModelSync = new ListSelectionModelSync();
	private ListSelectionModelSync sourceModelSync = new ListSelectionModelSync();

	private ListSelectionModel targetModel;
	private ListSelectionModel sourceModel;

	public void setTargetModel(ListSelectionModel targetModel) {
		if (this.targetModel != null) {
			this.targetModel.removeListSelectionListener(sourceModelSync);
		}
		this.targetModel = targetModel;
		if (this.targetModel != null) {
			targetModelSync.setTargetModel(targetModel, sourceModelSync);
			this.targetModel.addListSelectionListener(sourceModelSync);
		}
	}

	public void setSourceModel(ListSelectionModel listSelectionModel,
			RowSorter<?> rowSorter) {
		if (this.sourceModel != null) {
			this.sourceModel.removeListSelectionListener(targetModelSync);
		}

		if (rowSorter == null) {
			targetModelSync.setIndexConverter(null);
			sourceModelSync.setIndexConverter(null);
		} else {
			targetModelSync
					.setIndexConverter(new ViewToModelRowSorterConverter(
							rowSorter));
			sourceModelSync
					.setIndexConverter(new ModelToViewRowSorterConverter(
							rowSorter));
		}
		this.sourceModel = listSelectionModel;
		if (this.sourceModel != null) {
			sourceModelSync.setTargetModel(sourceModel, targetModelSync);
			this.sourceModel.addListSelectionListener(targetModelSync);
		}
	}

	private class ListSelectionModelSync implements ListSelectionListener {

		private ListSelectionModel targetModel;
		private IndexConverter indexConverter = NoopIndexConverter.INSTANCE;
		private ListSelectionListener targetModelSync;

		public void setTargetModel(ListSelectionModel targetModel,
				ListSelectionListener targetModelSync) {
			this.targetModel = targetModel;
			this.targetModelSync = targetModelSync;
		}

		public void setIndexConverter(IndexConverter indexConverter) {
			if (indexConverter == null) {
				indexConverter = NoopIndexConverter.INSTANCE;
			}
			this.indexConverter = indexConverter;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting() && targetModel != null) {
				ListSelectionModel sourceModel = (ListSelectionModel) e
						.getSource();
				int minIndex = sourceModel.getMinSelectionIndex();
				int maxIndex = sourceModel.getMaxSelectionIndex();

				targetModel.removeListSelectionListener(targetModelSync);

				targetModel.setValueIsAdjusting(true);
				targetModel.clearSelection();
				for (int index = minIndex; index <= maxIndex; index++) {
					if (sourceModel.isSelectedIndex(index)) {
						int targetIndex = indexConverter.convert(index);
						targetModel.addSelectionInterval(targetIndex,
								targetIndex);
					}
				}
				targetModel.setValueIsAdjusting(false);

				targetModel.addListSelectionListener(targetModelSync);
			}
		}

	}

	private static interface IndexConverter {

		public int convert(int index);
	}

	private static class ViewToModelRowSorterConverter implements
			IndexConverter {

		private RowSorter<?> rowSorter;

		public ViewToModelRowSorterConverter(RowSorter<?> rowSorter) {
			this.rowSorter = rowSorter;
		}

		@Override
		public int convert(int index) {
			return rowSorter.convertRowIndexToModel(index);
		}

	}

	private static class ModelToViewRowSorterConverter implements
			IndexConverter {

		private RowSorter<?> rowSorter;

		public ModelToViewRowSorterConverter(RowSorter<?> rowSorter) {
			this.rowSorter = rowSorter;
		}

		@Override
		public int convert(int index) {
			return rowSorter.convertRowIndexToView(index);
		}

	}

	private static class NoopIndexConverter implements IndexConverter {

		public static final NoopIndexConverter INSTANCE = new NoopIndexConverter();

		@Override
		public int convert(int index) {
			return index;
		}

	}

}
