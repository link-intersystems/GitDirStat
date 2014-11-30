package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class LoadRepositoryAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private Action[] actions;

	public LoadRepositoryAction(String name, Action... actions) {
		super(name);
		this.actions = actions;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for (Action action : actions) {
			action.actionPerformed(e);
		}

	}
}
