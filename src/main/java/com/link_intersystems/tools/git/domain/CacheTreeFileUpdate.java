package com.link_intersystems.tools.git.domain;

import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeFileUpdate implements TreeFileUpdate {

	private DirCacheEntry dirCacheEntry;

	public CacheTreeFileUpdate(DirCacheEntry dirCacheEntry) {
		this.dirCacheEntry = dirCacheEntry;
	}

	/* (non-Javadoc)
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#delete()
	 */
	@Override
	public void delete() {
		this.dirCacheEntry = null;
	}

	void apply(DirCacheBuilder builder) {
		if (dirCacheEntry != null) {
			builder.add(dirCacheEntry);
		}
	}

	/* (non-Javadoc)
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#getPath()
	 */
	@Override
	public String getPath() {
		return dirCacheEntry.getPathString();
	}

}
