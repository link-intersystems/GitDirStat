package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class PathDeletionIndexFilter implements IndexFilter {

	private Collection<String> pathsToDelete;

	public PathDeletionIndexFilter(Collection<String> pathsToDelete) {
		this.pathsToDelete = pathsToDelete;
	}

	@Override
	public void apply(CommitUpdate commitUpdate) throws IOException {
		TreeUpdate treeUpdate = commitUpdate.getTreeUpdate();
		Collection<String> actualPathsToDelete = new HashSet<String>(
				pathsToDelete);
		while (treeUpdate.hasNext()) {
			TreeFileUpdate fileUpdate = treeUpdate.next();
			String path = fileUpdate.getPath();
			if (actualPathsToDelete.remove(path)) {
				fileUpdate.delete();
			}
			if (actualPathsToDelete.isEmpty()) {
				break;
			}
		}
	}
}
