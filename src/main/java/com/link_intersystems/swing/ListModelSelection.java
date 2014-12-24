package com.link_intersystems.swing;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListModelSelection<E> extends AbstractPropertyChangeSupport
		implements SelectionModel<E> {

	private ListModel listModel;
	private ListDataListener listChangeAdapter = new ListModelChangeAdapter();

	private ListSelectionModel listSelectionModel;
	private ListSelectionListener selectionChangeAdapter = new ListSelectionModelChangeAdapter();

	public ListModelSelection(ListModel listModel,
			ListSelectionModel listSelectionModel) {
		this.listModel = listModel;
		this.listSelectionModel = listSelectionModel;

		listModel.addListDataListener(listChangeAdapter);
		listSelectionModel.addListSelectionListener(selectionChangeAdapter);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		super.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub
		super.addPropertyChangeListener(propertyName, listener);
	}

	public boolean isEmpty() {
		return listSelectionModel.isSelectionEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<E> getSelection() {
		int minIndex = listSelectionModel.getMinSelectionIndex();
		int maxIndex = listSelectionModel.getMaxSelectionIndex();

		List<E> selection = new ArrayList<E>();

		for (int i = minIndex; i <= maxIndex; i++) {
			if (listSelectionModel.isSelectedIndex(i)) {
				E selectedElement = (E) listModel.getElementAt(i);
				selection.add(selectedElement);
			}
		}

		return selection;
	}

	private void updateSelection() {
		firePropertyChange(PROP_SELECTION, null, null);
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
