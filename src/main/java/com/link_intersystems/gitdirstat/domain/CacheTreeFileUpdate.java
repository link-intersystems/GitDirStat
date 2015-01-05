package com.link_intersystems.gitdirstat.domain;

import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeFileUpdate implements TreeFileUpdate {

	private DirCacheEntry dirCacheEntry;
	private boolean delete;
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
		this.delete = true;
		cacheTreeUpdate.treeFileUpdated(this);
	}

	void apply(DirCacheBuilder builder) {
		if (delete) {
			return;
		}
		builder.add(dirCacheEntry);
	}

	public void move(String newpath) {
		DirCacheEntry oldCacheEntry = this.dirCacheEntry;
		dirCacheEntry = new DirCacheEntry(newpath);
		dirCacheEntry.setObjectId(oldCacheEntry.getObjectId());
		dirCacheEntry.setFileMode(oldCacheEntry.getFileMode());
		dirCacheEntry.setUpdateNeeded(true);

		cacheTreeUpdate.treeFileUpdated(this);
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
