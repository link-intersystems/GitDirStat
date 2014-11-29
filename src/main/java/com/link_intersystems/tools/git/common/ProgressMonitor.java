package com.link_intersystems.tools.git.common;

public interface ProgressMonitor {

	public static final int UNKNOWN = 0;

	public abstract void start(String taskName, int totalWork);

	public abstract void update(int completed);

	public abstract void end();
}
