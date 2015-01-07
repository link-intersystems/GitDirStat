package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.Collection;

public class PathDeletionIndexFilter implements IndexFilter {

	private Collection<String> pathsToDelete;

	public PathDeletionIndexFilter(Collection<String> pathsToDelete) {
		this.pathsToDelete = pathsToDelete;
	}

	@Override
	public void apply(CommitUpdate commitUpdate) throws IOException {
		TreeUpdate treeUpdate = commitUpdate.getTreeUpdate();
		while (treeUpdate.hasNext()) {
			TreeFileUpdate fileUpdate = treeUpdate.next();
			String path = fileUpdate.getPath();
			if (pathsToDelete.contains(path)) {
				fileUpdate.delete();
			}
		}
	}
}
