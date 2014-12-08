package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Iterator;

import com.link_intersystems.io.GlobPattern;

public class PathBranchFilter implements IndexFilter {

	private GlobPattern globPattern;

	public PathBranchFilter(GlobPattern globPattern) {
		this.globPattern = globPattern;
	}

	@Override
	public void filter(Index index) throws IOException {
		Iterator<IndexEntry> entryIterator = index.iterator();

		while (entryIterator.hasNext()) {
			IndexEntry indexEntry = entryIterator.next();
			String pathname = indexEntry.getPathname();
			boolean matches = globPattern.matches(pathname);
			if (matches) {
				entryIterator.remove();
			}
		}

	}

}
