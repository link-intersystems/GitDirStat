package com.link_intersystems.gitdirstat.ui;


public class GitDirStatUI {

	public static void main(String[] args) throws Exception {
		GitDirStatUIArguments dirStatUIArguments = GitDirStatUIArguments.parse(args);
		GUIApplication guiApplication = new GUIApplication();
		guiApplication.run(dirStatUIArguments);
	}
}
