package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

public class ListModelSelectionMediator<E> {

	private SelectionModel<? extends E> selectionModel;

	private class ListModelSelectionListener implements PropertyChangeListener {

		@SuppressWarnings("unchecked")
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ListModelSelection.PROP_SELECTION.equals(evt.getPropertyName())) {
				SelectionModel<E> source = (SelectionModel<E>) evt.getSource();
				ListModelSelectionMediator.this
						.handleListModelSelectionUpdate(source);
			}
		}

	}

	private ListModelSelectionListener listModelSelectionListener = new ListModelSelectionListener();
	private Set<Action> disabledOnEmptySelection = new HashSet<Action>();

	public ListModelSelectionMediator() {
	}

	public void setSelectionModel(SelectionModel<? extends E> selectionModel) {
		if (this.selectionModel != null) {
			this.selectionModel
					.removePropertyChangeListener(listModelSelectionListener);
		}
		this.selectionModel = selectionModel;
		if (this.selectionModel != null) {
			this.selectionModel
					.addPropertyChangeListener(listModelSelectionListener);
		}

	}

	public void addDisabledActionOnEmptySelection(Action action) {
		disabledOnEmptySelection.add(action);
		if (selectionModel != null) {
			handleListModelSelectionUpdate(selectionModel);
		}
	}

	private void handleListModelSelectionUpdate(
			SelectionModel<? extends E> selectionModel) {

		for (Action action : disabledOnEmptySelection) {
			action.setEnabled(!selectionModel.isEmpty());
		}
	}
}
