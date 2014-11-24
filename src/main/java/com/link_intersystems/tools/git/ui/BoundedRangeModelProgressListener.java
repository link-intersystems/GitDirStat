package com.link_intersystems.tools.git.ui;

import javax.swing.BoundedRangeModel;

import com.link_intersystems.tools.git.service.ProgressListener;

public class BoundedRangeModelProgressListener implements
		ProgressListener {
	private BoundedRangeModel progressModel;

	public BoundedRangeModelProgressListener(BoundedRangeModel progressModel) {
		this.progressModel = progressModel;
	}

	public void clear() {
		progressModel.setValue(0);
		progressModel.setMinimum(0);
		progressModel.setMaximum(0);
	}

	@Override
	public void start(int totalWork) {
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

}