package com.link_intersystems.tools.git.ui;

import com.link_intersystems.tools.git.common.ProgressMonitor;
import com.link_intersystems.tools.git.domain.ProgressListener;

public class ProgressListenerMonitorAdapter implements ProgressListener {

	private ProgressMonitor progressMonitor;
	private String taskName;

	public ProgressListenerMonitorAdapter(ProgressMonitor progressMonitor,
			String taskName) {
		this.progressMonitor = progressMonitor;
		this.taskName = taskName;
	}

	@Override
	public void update(int completed) {
		progressMonitor.update(completed);
	}

	@Override
	public void start(int totalWork) {
		progressMonitor.start(taskName, totalWork);
	}

	@Override
	public void end() {
		progressMonitor.end();
	}

}
