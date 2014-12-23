package com.link_intersystems.gitdirstat;

import com.link_intersystems.gitdirstat.cli.FilterGitRepoApplication;

public class GitDirStatFilterBranchCLI {

	public static void main(String[] args) {
		GitDirStatMainAdapter.executeApplication(
				new FilterGitRepoApplication(), args);
	}
}
