package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import com.link_intersystems.io.GlobPattern;

public class GlobPathIndexFilter implements IndexFilter {

	private GlobPattern globPattern;

	public GlobPathIndexFilter(GlobPattern globPattern) {
		this.globPattern = globPattern;
	}

	@Override
	public void apply(CommitUpdate commitUpdate) throws IOException {
		TreeUpdate treeUpdate = commitUpdate.getTreeUpdate();

		while (treeUpdate.hasNext()) {
			TreeFileUpdate treeFileUpdate = treeUpdate.next();
			String path = treeFileUpdate.getPath();
			if (globPattern.matches(path)) {
				treeFileUpdate.delete();
			}
		}
	}

}
