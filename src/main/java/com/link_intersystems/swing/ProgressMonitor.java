package com.link_intersystems.swing;

public interface ProgressMonitor {

	public static final int UNKNOWN = -1;

	public abstract void start(String taskName, int totalWork);

	public abstract void update(int completed);

	public abstract void end();

	boolean isCanceled();
}
