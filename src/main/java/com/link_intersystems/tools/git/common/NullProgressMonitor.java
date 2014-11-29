package com.link_intersystems.tools.git.common;

public class NullProgressMonitor implements ProgressMonitor {

	public static final ProgressMonitor INSTANCE = new NullProgressMonitor();

	@Override
	public void start(String taskName, int totalWork) {
	}

	@Override
	public void update(int completed) {
	}

	@Override
	public void end() {
	}

}
