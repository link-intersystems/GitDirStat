package com.link_intersystems.tools.git.ui;

import java.awt.Component;

import javax.swing.SwingUtilities;

import com.link_intersystems.tools.git.common.ProgressMonitor;

public class ProgressMonitorComponentVisibilityAdapter implements
		ProgressMonitor {

	private Component[] visibleOnProgress;
	private ProgressMonitor progressMonitor;

	public ProgressMonitorComponentVisibilityAdapter(
			ProgressMonitor progressMonitor, Component... visibleOnProgress) {
		this.progressMonitor = progressMonitor;
		this.visibleOnProgress = visibleOnProgress;
	}

	@Override
	public void start(int totalWork) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component component : visibleOnProgress) {
					component.setVisible(true);
				}
			}
		});
		progressMonitor.start(totalWork);

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

}
