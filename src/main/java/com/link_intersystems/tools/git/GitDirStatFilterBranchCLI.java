package com.link_intersystems.tools.git;

import com.link_intersystems.tools.git.cli.FilterGitRepoApplication;

public class GitDirStatFilterBranchCLI {

	public static void main(String[] args) {
		GitDirStatMainAdapter.executeApplication(
				new FilterGitRepoApplication(), args);
	}
}
