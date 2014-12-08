package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class ProgressAction extends AbstractAction {

	private static final long serialVersionUID = -4286176570489467961L;
	public static final String PROP_RUNNING = "running";

	private boolean running;

	@Override
	public void actionPerformed(ActionEvent e) {
		setRunning(true);
		try {
			doActionPerformed(e);
		} finally {
			setRunning(false);
		}
	}

	protected void doActionPerformed(ActionEvent e){
	}

	public boolean isRunning() {
		return running;
	}

	protected void setRunning(boolean running) {
		firePropertyChange(PROP_RUNNING, this.running, this.running = running);
	}
}
