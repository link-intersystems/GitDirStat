package com.link_intersystems.tools.git.ui.metrics;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public abstract class AbstractListModelTableModelAdapter<E> extends
		AbstractTableModel {

	private static final long serialVersionUID = -1842085583184116258L;

	private class ListModelSync implements ListDataListener {

		@Override
		public void intervalAdded(ListDataEvent e) {
			fireTableDataChanged();
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			fireTableDataChanged();
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			fireTableDataChanged();
		}

	}

	private ListModelSync listModelSync = new ListModelSync();
	private ListModel entryModel = new DefaultListModel();

	public AbstractListModelTableModelAdapter() {
		entryModel.addListDataListener(listModelSync);
	}

	public void setEntryModel(ListModel entryModel) {
		if (this.entryModel != null) {
			this.entryModel.removeListDataListener(listModelSync);
		}
		if (entryModel == null) {
			entryModel = new DefaultListModel();
		}
		this.entryModel = entryModel;
		if (this.entryModel != null) {
			this.entryModel.addListDataListener(listModelSync);
		}
	}

	public ListModel getEntryModel() {
		return entryModel;
	}

	@Override
	public int getRowCount() {
		return entryModel.getSize();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ListModel entryModel = getEntryModel();
		E elementAt = (E) entryModel.getElementAt(rowIndex);
		return getColumnValue( elementAt, columnIndex);
	}

	protected abstract Object getColumnValue(E element, int columnIndex);

}