package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import com.link_intersystems.io.GlobPattern;

public class PathBranchFilter implements IndexFilter {

	@SuppressWarnings("unused")
	private GlobPattern globPattern;

	public PathBranchFilter(GlobPattern globPattern) {
		this.globPattern = globPattern;
	}

	@Override
	public void apply(CommitUpdate commitUpdate) throws IOException {
		// TODO Auto-generated method stub

	}

}
