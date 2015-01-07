package com.link_intersystems.beans;

import java.beans.PropertyChangeListener;

public interface PropertyChangeListenerBinding {

	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);
}