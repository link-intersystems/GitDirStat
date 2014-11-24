package com.link_intersystems.tools.git.ui;

import java.awt.Component;

import javax.swing.SwingUtilities;

import com.link_intersystems.tools.git.service.ProgressListener;

public class ComponentVisibleOnProgress implements ProgressListener {

	private Component[] components;
	private ProgressListener progressListener;

	public ComponentVisibleOnProgress(ProgressListener progressListener,
			Component... components) {
		this.progressListener = progressListener;
		this.components = components;
	}

	@Override
	public void start(int totalWork) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component component : components) {
					component.setVisible(true);
				}
			}
		});
		progressListener.start(totalWork);

	}

	@Override
	public void update(int completed) {
		progressListener.update(completed);
	}

	@Override
	public void end() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (Component component : components) {
					component.setVisible(false);
				}
			}
		});
		progressListener.end();
	}

}
