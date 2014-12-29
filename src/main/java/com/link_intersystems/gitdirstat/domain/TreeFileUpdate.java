package com.link_intersystems.gitdirstat.domain;

public interface TreeFileUpdate {

	public abstract void delete();

	public abstract String getPath();

	void move(String newpath);

}