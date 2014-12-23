package com.link_intersystems.gitdirstat;

import com.link_intersystems.gitdirstat.cli.CommandLineGitDirStatApplication;

public class GitDirStatCLI {

	public static void main(String[] args) {
		GitDirStatMainAdapter.executeApplication(
				new CommandLineGitDirStatApplication(), args);
	}
}
