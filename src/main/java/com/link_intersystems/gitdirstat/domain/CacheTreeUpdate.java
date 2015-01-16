package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEditor.DeletePath;
import org.eclipse.jgit.dircache.DirCacheEditor.PathEdit;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;

public class CacheTreeUpdate implements TreeUpdate {

	private Collection<CacheTreeFileUpdate> treeFiles = new ArrayList<CacheTreeFileUpdate>();
	private CacheTreeFileUpdate actualTreeFile;
	private DirCache index;
	private int dirCacheEntryIndex = 0;
	private Collection<PathEdit> pathEdits = new ArrayList<PathEdit>();

	public CacheTreeUpdate(IndexUpdate indexUpdate, Commit commit) {
		try {
			index = indexUpdate.resetDirCache(commit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Apply the changes that this {@link TreeUpdate} represents to the given
	 * {@link DirCache}. The {@link DirCache} will be unlocked if was modified.
	 *
	 * @param dirCache
	 * @return true if updates are applied to the {@link DirCache}, false if the
	 *         {@link DirCache} has not been modified.
	 */
	public ObjectId apply(ObjectInserter objectInserter) {
		ObjectId newTreeId = null;
		if (hasUpdates()) {
			DirCacheEditor editor = index.editor();

			for (PathEdit pathEdit : pathEdits) {
				editor.add(pathEdit);
			}

			editor.finish();

			try {
				// Write the index as tree to the object database. This may
				// fail for example when the index contains unmerged paths
				// (unresolved conflicts)
				newTreeId = index.writeTree(objectInserter);
			} catch (IOException e) {
				throw new GitRepositoryException(e);
			}
		}
		return newTreeId;
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

	public boolean hasUpdates() {
		return !pathEdits.isEmpty();
	}

	void registerPathEdit(DeletePath pathEdit) {
		pathEdits.add(pathEdit);
	}

}
