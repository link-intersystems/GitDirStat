package com.link_intersystems.tools.git;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class GitDirStat {

	public static void main(String[] args) {
		try {
			GitDirStatArguments gitDirStatArguments = null;
			GitDirStatApplication gitDirStatApplication = createGitDirStatApplication(gitDirStatArguments);
			gitDirStatApplication.run(gitDirStatArguments);
		} catch (GitDirStatArgumentsParseException e) {
			printHelp(e.getOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java " + GitDirStat.class.getName(), options);
	}

	private static GitDirStatApplication createGitDirStatApplication(
			GitDirStatArguments gitDirStatArguments) {
		GitDirStatApplication gitDirStatApplication = null;
		return gitDirStatApplication;
	}
}
