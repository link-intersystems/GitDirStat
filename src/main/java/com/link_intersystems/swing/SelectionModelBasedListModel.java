package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

public class SelectionModelBasedListModel extends AbstractListModel {

	private static final long serialVersionUID = 2230029695674837365L;

	private class SelectionModelUpdateDelegate implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			SelectionModel<?> selectionModel = (SelectionModel<?>) evt
					.getSource();
			fireContentsChanged(this, 0, selectionModel.getSelection().size());
		}

	}

	private SelectionModelUpdateDelegate selectionModelUpdateDelegate = new SelectionModelUpdateDelegate();
	private SelectionModel<?> selectionModel;

	public SelectionModelBasedListModel() {
	}

	public void setSelectionModel(SelectionModel<?> selectionModel) {
		if (this.selectionModel != null) {
			this.selectionModel
					.removePropertyChangeListener(
							SelectionModel.PROP_SELECTION,
							selectionModelUpdateDelegate);
		}
		this.selectionModel = selectionModel;
		if (this.selectionModel != null) {
			this.selectionModel
					.addPropertyChangeListener(SelectionModel.PROP_SELECTION,
							selectionModelUpdateDelegate);
			fireContentsChanged(this, 0, selectionModel.getSelection().size());
		}

	}

	@Override
	public int getSize() {
		return selectionModel.getSelection().size();
	}

	@Override
	public Object getElementAt(int index) {
		return selectionModel.getSelection().get(index);
	}

}
