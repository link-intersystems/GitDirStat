package com.link_intersystems.gitdirstat;

import com.link_intersystems.gitdirstat.ui.GUIApplication;

public class GitDirStatUI {

	public static void main(String[] args) {
		GitDirStatMainAdapter.executeApplication(new GUIApplication(), args);
	}
}
