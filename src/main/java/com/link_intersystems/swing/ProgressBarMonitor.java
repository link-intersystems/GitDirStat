package com.link_intersystems.swing;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

public class ProgressBarMonitor implements ProgressMonitor {

	private JProgressBar progressBar;

	public ProgressBarMonitor(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public void clear() {
		BoundedRangeModel progressModel = progressBar.getModel();
		progressModel.setValue(0);
		progressModel.setMinimum(0);
		progressModel.setMaximum(0);
		progressBar.setString("");
	}

	@Override
	public void start(String taskName, int totalWork) {
		clear();
		progressBar.setString(taskName);
		BoundedRangeModel progressModel = progressBar.getModel();
		progressModel.setMaximum(totalWork);
	}

	@Override
	public void update(int completed) {
		BoundedRangeModel progressModel = progressBar.getModel();
		int newValue = progressModel.getValue();
		progressModel.setValue(newValue + 1);
	}

	@Override
	public void end() {
		BoundedRangeModel progressModel = progressBar.getModel();
		progressModel.setValue(progressModel.getMaximum());
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

}
