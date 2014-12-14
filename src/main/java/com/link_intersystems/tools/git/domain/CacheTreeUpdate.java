package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;

public class CacheTreeUpdate implements TreeUpdate {

	private Collection<CacheTreeFileUpdate> treeFiles = new ArrayList<CacheTreeFileUpdate>();
	private CacheTreeFileUpdate actualTreeFile;
	private DirCache index;
	private int dirCacheEntryIndex = 0;


	public CacheTreeUpdate(DirCache index) {
		this.index = index;
	}

	void apply(DirCache dirCache) throws IOException {
		try {
			DirCacheBuilder builder = dirCache.builder();

			for (CacheTreeFileUpdate treeFile : treeFiles) {
				treeFile.apply(builder);
			}

			builder.commit();
		} catch (IOException e) {
			throw new RuntimeException(e);
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
		actualTreeFile = new CacheTreeFileUpdate(dirCacheEntry);
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

}
