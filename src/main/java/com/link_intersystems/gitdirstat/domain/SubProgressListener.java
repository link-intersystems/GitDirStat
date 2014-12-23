package com.link_intersystems.gitdirstat.domain;

import com.link_intersystems.math.LinearEquation;
import com.link_intersystems.math.TwoPointLinearEquation;

public class SubProgressListener implements ProgressListener {

	private ProgressListener progressListener;
	private int ticks;
	private LinearEquation linearEquation;
	private int completed;
	private double lastFx;

	public SubProgressListener(ProgressListener progressListener, int ticks) {
		this.progressListener = progressListener;
		this.ticks = ticks;
	}

	@Override
	public void start(int totalWork) {
		linearEquation = new TwoPointLinearEquation(totalWork, ticks);
	}

	@Override
	public void update(int completed) {
		this.completed += completed;
		double fX = linearEquation.fX(this.completed);
		int subUpdated = (int) (fX - lastFx);
		if (subUpdated > 0) {
			progressListener.update(subUpdated);
			this.lastFx = fX;
		}

	}

	@Override
	public void end() {
		progressListener.update(ticks);
	}

	@Override
	public boolean isCanceled() {
		return progressListener.isCanceled();
	}

}
