package com.link_intersystems.tools.git;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.link_intersystems.tools.git.cli.CommandLineGitDirStatApplication;

public class GitDirStatCLI {

	public static void main(String[] args) {
		try {
			GitDirStatArguments gitDirStatArguments = CommandLineGitDirStatArguments
					.parse(args);
			GitDirStatApplication gitDirStatApplication = new CommandLineGitDirStatApplication();
			gitDirStatApplication.run(gitDirStatArguments);
		} catch (GitDirStatArgumentsParseException e) {
			printHelp(e.getOptions());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("java " + GitDirStatCLI.class.getName(),
				options);
	}
}
