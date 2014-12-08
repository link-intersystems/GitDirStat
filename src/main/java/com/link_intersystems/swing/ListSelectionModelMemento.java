package com.link_intersystems.swing;

import java.io.Serializable;
import java.util.BitSet;

import javax.swing.ListSelectionModel;

public class ListSelectionModelMemento implements Serializable, Cloneable {

	private static final long serialVersionUID = -7208592596126194634L;
	private int minIndex;
	private int maxIndex;
	private BitSet selectionMemento = new BitSet();

	public void save(ListSelectionModel listSelectionModel) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxIndex;
		result = prime * result + minIndex;
		result = prime
				* result
				+ ((selectionMemento == null) ? 0 : selectionMemento.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListSelectionModelMemento other = (ListSelectionModelMemento) obj;
		if (maxIndex != other.maxIndex)
			return false;
		if (minIndex != other.minIndex)
			return false;
		if (selectionMemento == null) {
			if (other.selectionMemento != null)
				return false;
		} else if (!selectionMemento.equals(other.selectionMemento))
			return false;
		return true;
	}

	@Override
	public ListSelectionModelMemento clone() {
		try {
			ListSelectionModelMemento clone = (ListSelectionModelMemento) super
					.clone();
			clone.maxIndex = maxIndex;
			clone.maxIndex = minIndex;
			clone.selectionMemento = (BitSet) selectionMemento.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}
