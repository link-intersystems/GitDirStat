package com.link_intersystems.tools.git.ui;

import javax.swing.BoundedRangeModel;

import com.link_intersystems.swing.ProgressMonitor;

public class BoundedRangeModelProgressListener implements ProgressMonitor {
	private BoundedRangeModel progressModel;
	private boolean canceled;

	public BoundedRangeModelProgressListener(BoundedRangeModel progressModel) {
		this.progressModel = progressModel;
	}

	public void clear() {
		progressModel.setValue(0);
		progressModel.setMinimum(0);
		progressModel.setMaximum(0);
		canceled = false;
	}

	@Override
	public void start(String taskName, int totalWork) {
		clear();
		progressModel.setMaximum(totalWork);
	}

	@Override
	public void update(int completed) {
		int newValue = progressModel.getValue();
		progressModel.setValue(newValue + 1);
	}

	@Override
	public void end() {
		progressModel.setValue(progressModel.getMaximum());
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}