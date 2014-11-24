package com.link_intersystems.tools.git;

import com.link_intersystems.tools.git.cli.CommandLineGitDirStatApplication;

public class GitDirStatCLI {

	public static void main(String[] args) {
		GitDirStatMainAdapter.executeApplication(
				new CommandLineGitDirStatApplication(), args);
	}
}
