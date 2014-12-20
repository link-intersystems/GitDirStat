package com.link_intersystems.swing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListModelSelection<E> {

	public static final String PROP_SELECTION = "selection";

	private ListModel listModel;
	private ListDataListener listChangeAdapter = new ListModelChangeAdapter();

	private ListSelectionModel listSelectionModel;
	private ListSelectionListener selectionChangeAdapter = new ListSelectionModelChangeAdapter();

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private List<E> selection;
	private RowSorter<?> rowSorter;

	public ListModelSelection(ListModel listModel,
			ListSelectionModel listSelectionModel) {
		this.listModel = listModel;
		this.listSelectionModel = listSelectionModel;

		listModel.addListDataListener(listChangeAdapter);
		listSelectionModel.addListSelectionListener(selectionChangeAdapter);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	public void setRowSorter(RowSorter<?> rowSorter) {
		this.rowSorter = rowSorter;
	}

	public List<E> getSelection() {
		return selection;
	}

	protected void setSelection(List<E> selection) {
		List<E> oldSelection = this.selection;
		this.selection = new ArrayList<E>(selection);

		propertyChangeSupport.firePropertyChange(PROP_SELECTION, oldSelection,
				this.selection);
	}

	@SuppressWarnings("unchecked")
	private void updateSelection() {
		int minIndex = listSelectionModel.getMinSelectionIndex();
		int maxIndex = listSelectionModel.getMaxSelectionIndex();

		List<E> selection = new ArrayList<E>();

		for (int i = minIndex; i <= maxIndex; i++) {
			if (listSelectionModel.isSelectedIndex(i)) {
				int selectionIndex = i;
				if (rowSorter != null) {
					selectionIndex = rowSorter
							.convertRowIndexToModel(selectionIndex);
				}
				E selectedElement = (E) listModel.getElementAt(selectionIndex);
				selection.add(selectedElement);
			}
		}

		setSelection(selection);
	}

	private class ListModelChangeAdapter implements ListDataListener {

		@Override
		public void intervalAdded(ListDataEvent e) {
			listSelectionModel.clearSelection();
			updateSelection();
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			listSelectionModel.clearSelection();
			updateSelection();
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			listSelectionModel.clearSelection();
			updateSelection();
		}

	}

	private class ListSelectionModelChangeAdapter implements
			ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			updateSelection();
		}

	}
}
