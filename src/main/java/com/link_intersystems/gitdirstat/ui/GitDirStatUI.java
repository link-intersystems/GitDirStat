package com.link_intersystems.gitdirstat.ui;

import com.link_intersystems.gitdirstat.GitDirStatArgumentsParseException;

public class GitDirStatUI {

	public static void main(String[] args) throws Exception {
		try {
			GitDirStatUIArguments dirStatUIArguments = GitDirStatUIArguments
					.parse(args);
			GUIApplication guiApplication = new GUIApplication();
			guiApplication.run(dirStatUIArguments);
		} catch (GitDirStatArgumentsParseException e) {
			e.printHelp(System.err);
		}
	}
}
