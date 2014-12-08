package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;

import javax.swing.Action;

public class SingleActionSelectionMediator implements PropertyChangeListener {

	public static final String PROP_SELECTED_ACTION = "selectedAction";

	private Action[] actions;
	private PropertyChangeListener selectedKeyListener = new PropertyChangeListenerProxy(
			Action.SELECTED_KEY, this);
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private Action selectedAction;

	public void setActionGroup(Action... actions) {
		if (this.actions != null) {
			for (Action action : this.actions) {
				action.removePropertyChangeListener(selectedKeyListener);
			}
		}

		this.actions = actions;

		for (Action action : this.actions) {
			action.addPropertyChangeListener(selectedKeyListener);
			Boolean selectedKey = (Boolean) action
					.getValue(Action.SELECTED_KEY);
			boolean isSelected = selectedKey != null && selectedKey;
			if (isSelected) {
				if (selectedAction == null) {
					setSelectedAction(action);
				} else {
					throw new IllegalArgumentException(
							"actions must contain at most 1 selected action");
				}
			}
		}
	}

	private void setSelectedAction(Action selectedAction) {
		propertyChangeSupport.firePropertyChange(PROP_SELECTED_ACTION,
				this.selectedAction, this.selectedAction = selectedAction);
	}

	public Action getSelectedAction() {
		return selectedAction;
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		Boolean selectedKey = (Boolean) evt.getNewValue();

		if (selectedKey) {
			for (Action action : actions) {
				if (action.equals(source)) {
					continue;
				}
				action.putValue(Action.SELECTED_KEY, Boolean.FALSE);
			}
		}
	}

}
