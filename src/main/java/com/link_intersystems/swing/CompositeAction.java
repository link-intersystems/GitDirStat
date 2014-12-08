package com.link_intersystems.swing;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class CompositeAction extends AbstractAction implements
		PropertyChangeListener {

	private static final long serialVersionUID = 3846981811584494081L;

	ActionPropertySync propertySync = new ActionPropertySync(this);
	private List<Action> actions = new ArrayList<Action>();

	public CompositeAction(Action mainAction, Action... actions) {
		this.actions.add(mainAction);
		this.actions.addAll(Arrays.asList(actions));
		propertySync.setSynchronization(mainAction);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		for (Action action : actions) {
			if (action.isEnabled()) {
				action.actionPerformed(e);
			}
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

}
