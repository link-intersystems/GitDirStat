package com.link_intersystems.swing;

import java.awt.Component;

import javax.swing.SwingUtilities;

public class ComponentVisibilityOnProgress implements ProgressMonitor {

	private Component[] visibleOnProgress;
	private ProgressMonitor progressMonitor;

	public ComponentVisibilityOnProgress(ProgressMonitor progressMonitor,
			Component... visibleOnProgress) {
		this.progressMonitor = progressMonitor;
		this.visibleOnProgress = visibleOnProgress;
	}

	@Override
	public void start(String taskName, int totalWork) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component component : visibleOnProgress) {
					component.setVisible(true);
				}
			}
		});
		progressMonitor.start(taskName, totalWork);

	}

	@Override
	public void update(int completed) {
		progressMonitor.update(completed);
	}

	@Override
	public void end() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component component : visibleOnProgress) {
					component.setVisible(false);
				}
			}
		});
		progressMonitor.end();
	}

	@Override
	public boolean isCanceled() {
		return progressMonitor.isCanceled();
	}

}
