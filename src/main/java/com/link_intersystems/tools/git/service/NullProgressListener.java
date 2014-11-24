package com.link_intersystems.tools.git.service;

public class NullProgressListener implements ProgressListener {

	public static final ProgressListener INSTANCE = new NullProgressListener();

	@Override
	public void start(int totalWork) {
	}

	@Override
	public void update(int completed) {
	}

	@Override
	public void end() {
	}

}
