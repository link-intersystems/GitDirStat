package com.link_intersystems.gitdirstat;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class GitDirStatMainAdapter {

	public static void executeApplication(
			GitDirStatApplication gitDirStatApplication, String[] args) {
		try {
			GitDirStatArguments gitDirStatArguments = CommandLineGitDirStatArguments
					.parse(args);
			gitDirStatApplication.run(gitDirStatArguments);
		} catch (GitDirStatArgumentsParseException e) {
			printHelp(e.getOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(
				"java " + GitDirStatMainAdapter.class.getName(), options);
	}
}
