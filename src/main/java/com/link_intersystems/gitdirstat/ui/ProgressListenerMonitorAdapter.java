package com.link_intersystems.gitdirstat.ui;

import com.link_intersystems.gitdirstat.domain.ProgressListener;
import com.link_intersystems.swing.ProgressMonitor;

public class ProgressListenerMonitorAdapter implements ProgressListener {

	private ProgressMonitor progressMonitor;
	private String taskName;
	private long updateIntervalMs;
	private int completed;
	private long nextUpdate;

	public ProgressListenerMonitorAdapter(ProgressMonitor progressMonitor,
			String taskName) {
		this.progressMonitor = progressMonitor;
		this.taskName = taskName;
	}

	public void setUpdateInterval(long updateIntervalMs) {
		this.updateIntervalMs = updateIntervalMs;
	}

	@Override
	public void update(int completed) {
		this.completed += completed;
		long now = System.currentTimeMillis();
		if (now > nextUpdate) {
			progressMonitor.update(this.completed);
			nextUpdate = now + updateIntervalMs;
			this.completed = 0;
		}
	}

	@Override
	public void start(int totalWork) {
		progressMonitor.start(taskName, totalWork);
	}

	@Override
	public void end() {
		progressMonitor.end();
		this.completed = 0;
		nextUpdate = 0;
	}

	@Override
	public boolean isCanceled() {
		return progressMonitor.isCanceled();
	}

}
