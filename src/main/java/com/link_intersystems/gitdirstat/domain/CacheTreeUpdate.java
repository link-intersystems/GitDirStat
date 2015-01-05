package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeUpdate implements TreeUpdate {

	private Collection<CacheTreeFileUpdate> treeFiles = new ArrayList<CacheTreeFileUpdate>();
	private CacheTreeFileUpdate actualTreeFile;
	private DirCache index;
	private int dirCacheEntryIndex = 0;
	private boolean hasTreeFileUpdates;

	public CacheTreeUpdate(DirCache index) {
		this.index = index;
	}

	/**
	 * Apply the changes that this {@link TreeUpdate} represents to the given
	 * {@link DirCache}. The {@link DirCache} will be unlocked if was modified.
	 *
	 * @param dirCache
	 * @return true if updates are applied to the {@link DirCache}, false if the
	 *         {@link DirCache} has not been modified.
	 */
	boolean apply(DirCache dirCache) {
		if (hasTreeFileUpdates) {
			try {
				DirCacheEditor editor = dirCache.editor();

				for (CacheTreeFileUpdate treeFile : treeFiles) {
					treeFile.apply(editor);
				}

				editor.commit();
			} catch (IOException e) {
				throw new GitRepositoryException(e);
			}
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return dirCacheEntryIndex < index.getEntryCount();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#next()
	 */
	@Override
	public TreeFileUpdate next() {
		DirCacheEntry dirCacheEntry = index.getEntry(dirCacheEntryIndex++);
		actualTreeFile = new CacheTreeFileUpdate(dirCacheEntry, this);
		treeFiles.add(actualTreeFile);
		return actualTreeFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#remove()
	 */
	@Override
	public void remove() {
		if (actualTreeFile != null) {
			actualTreeFile.delete();
		}
	}

	void treeFileUpdated(CacheTreeFileUpdate cacheTreeFileUpdate) {
		hasTreeFileUpdates = true;
	}

	public boolean hasUpdates() {
		return hasTreeFileUpdates;
	}

}
