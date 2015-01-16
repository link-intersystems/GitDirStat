package com.link_intersystems.gitdirstat.domain;

import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeFileUpdate implements TreeFileUpdate {

	private DirCacheEntry dirCacheEntry;
	private CacheTreeUpdate cacheTreeUpdate;

	public CacheTreeFileUpdate(DirCacheEntry dirCacheEntry,
			CacheTreeUpdate cacheTreeUpdate) {
		this.dirCacheEntry = dirCacheEntry;
		this.cacheTreeUpdate = cacheTreeUpdate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#delete()
	 */
	@Override
	public void delete() {
		DirCacheEditor.DeletePath deletePath = new DirCacheEditor.DeletePath(
				dirCacheEntry);
		cacheTreeUpdate.registerPathEdit(deletePath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#getPath()
	 */
	@Override
	public String getPath() {
		return dirCacheEntry.getPathString();
	}

}
