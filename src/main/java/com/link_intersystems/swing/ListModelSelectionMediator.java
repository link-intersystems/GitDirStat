package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

public class ListModelSelectionMediator<E> {

	private ListModelSelection<? extends E> listModelSelection;

	private class ListModelSelectionListener implements PropertyChangeListener {

		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ListModelSelection.PROP_SELECTION.equals(evt.getPropertyName())) {
				ListModelSelection<E> source = (ListModelSelection<E>) evt
						.getSource();
				ListModelSelectionMediator.this
						.handleListModelSelectionUpdate(source);
			}
		}

	}

	private ListModelSelectionListener listModelSelectionListener = new ListModelSelectionListener();
	private Set<Action> disabledOnEmptySelection = new HashSet<Action>();

	public ListModelSelectionMediator() {
	}

	public void setListModelSelection(
			ListModelSelection<? extends E> listModelSelection) {
		if (this.listModelSelection != null) {
			this.listModelSelection
					.removePropertyChangeListener(listModelSelectionListener);
		}
		this.listModelSelection = listModelSelection;
		if (this.listModelSelection != null) {
			this.listModelSelection
					.addPropertyChangeListener(listModelSelectionListener);
		}

	}

	public void addDisabledActionOnEmptySelection(Action action) {
		disabledOnEmptySelection.add(action);
		if (listModelSelection != null) {
			handleListModelSelectionUpdate(listModelSelection);
		}
	}

	private void handleListModelSelectionUpdate(
			ListModelSelection<? extends E> listModelSelection) {

		for (Action action : disabledOnEmptySelection) {
			action.setEnabled(!listModelSelection.isEmpty());
		}
	}
}
