package com.link_intersystems.gitdirstat.domain;

import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeFileUpdate implements TreeFileUpdate {

	private DirCacheEntry dirCacheEntry;
	private boolean delete;

	public CacheTreeFileUpdate(DirCacheEntry dirCacheEntry) {
		this.dirCacheEntry = dirCacheEntry;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#delete()
	 */
	@Override
	public void delete() {
		this.delete = true;
	}

	void apply(DirCacheBuilder builder) {
		if (delete) {
			return;
		}
		builder.add(dirCacheEntry);
	}

	public void move(String newpath){
		DirCacheEntry oldCacheEntry = this.dirCacheEntry;
		dirCacheEntry = new DirCacheEntry(newpath);
		dirCacheEntry.setObjectId(oldCacheEntry.getObjectId());
		dirCacheEntry.setFileMode(oldCacheEntry.getFileMode());
		dirCacheEntry.setUpdateNeeded(true);
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
