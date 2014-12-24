package com.link_intersystems.swing;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface SelectionModel<E> {

	public static final String PROP_SELECTION = "selection";

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);

	void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener);

	boolean isEmpty();

	List<E> getSelection();

}
