package com.link_intersystems.gitdirstat.ui;

import java.util.Locale;

import com.link_intersystems.gitdirstat.GitDirStatArgumentsParseException;

public class GitDirStatUI {

	public static void main(String[] args) throws Exception {
		try {
			Locale.setDefault(Locale.US);
			GitDirStatUIArguments dirStatUIArguments = GitDirStatUIArguments
					.parse(args);
			GUIApplication guiApplication = new GUIApplication();
			guiApplication.run(dirStatUIArguments);
		} catch (GitDirStatArgumentsParseException e) {
			e.printHelp(System.err);
		}
	}
}
