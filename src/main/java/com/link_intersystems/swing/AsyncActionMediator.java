package com.link_intersystems.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.Action;

public class AsyncActionMediator implements PropertyChangeListener {

	Collection<Action> disabledActionsWhileRunning = new HashSet<Action>();
	Collection<Action> enabledActionsWhileRunning = new HashSet<Action>();

	public AsyncActionMediator(AsyncProgressAction<?, ?> asyncAction) {
		asyncAction.addPropertyChangeListener(this);
	}

	public void addDisabledActionWhileRunning(Action action) {
		disabledActionsWhileRunning.add(action);
	}

	public void addEnabledActionWhileRunning(Action action) {
		enabledActionsWhileRunning.add(action);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ProgressAction.PROP_RUNNING.equals(evt.getPropertyName())) {
			Boolean running = (Boolean) evt.getNewValue();
			for (Action action : disabledActionsWhileRunning) {
				action.setEnabled(!running);
			}
			for (Action action : enabledActionsWhileRunning) {
				action.setEnabled(running);
			}
		}
	}
}
