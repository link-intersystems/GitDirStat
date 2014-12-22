package com.link_intersystems.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class ProgressAction extends AbstractAction {

	private static final long serialVersionUID = -4286176570489467961L;
	private ProgressMonitor progressMonitor = NullProgressMonitor.INSTANCE;

	public static final String PROP_RUNNING = "running";

	private ProgressActionMediator actionMediator = new ProgressActionMediator(
			this);

	private boolean running;

	public void addDisabledActionWhileRunning(Action action) {
		actionMediator.addDisabledActionWhileRunning(action);
	}

	public void addEnabledActionWhileRunning(Action action) {
		actionMediator.addEnabledActionWhileRunning(action);
	}

	private boolean disableWhileRunning;

	public void setDisableWhileRunning(boolean disableWhileRunning) {
		this.disableWhileRunning = disableWhileRunning;
	}

	@Override
	public final void actionPerformed(ActionEvent e) {
		try {
			setRunning(true);
			doActionPerformed(e, progressMonitor);
		} finally {
			setRunning(false);
			progressMonitor.end();
		}
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		if (progressMonitor == null) {
			progressMonitor = NullProgressMonitor.INSTANCE;
		}
		this.progressMonitor = progressMonitor;
	}

	protected void doActionPerformed(ActionEvent e,
			ProgressMonitor progressMonitor) {
	}

	public boolean isRunning() {
		return running;
	}

	protected void setRunning(boolean running) {
		if (running) {
			if (disableWhileRunning) {
				setEnabled(false);
			}
		} else {
			setEnabled(true);
		}
		firePropertyChange(PROP_RUNNING, this.running, this.running = running);
	}

}
