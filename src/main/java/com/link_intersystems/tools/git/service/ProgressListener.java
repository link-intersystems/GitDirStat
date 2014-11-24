package com.link_intersystems.tools.git.service;

public interface ProgressListener {

	public static final int UNKNOWN = 0;

	public abstract void start(int totalWork);

	public abstract void update(int completed);

	public abstract void end();
}
