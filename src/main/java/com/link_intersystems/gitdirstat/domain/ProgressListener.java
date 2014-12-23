package com.link_intersystems.gitdirstat.domain;

public interface ProgressListener {
	public static final int UNKNOWN = -1;

	public abstract void start(int totalWork);

	public abstract void update(int completed);

	public abstract void end();

	public abstract boolean isCanceled();
}
