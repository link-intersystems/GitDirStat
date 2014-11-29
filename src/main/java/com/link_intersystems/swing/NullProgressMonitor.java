package com.link_intersystems.swing;


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

	@Override
	public boolean isCanceled() {
		return false;
	}

}
