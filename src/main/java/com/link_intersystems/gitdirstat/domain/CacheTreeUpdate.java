package com.link_intersystems.gitdirstat.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.dircache.DirCacheEditor.DeletePath;
import org.eclipse.jgit.dircache.DirCacheEditor.PathEdit;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FS;

public class CacheTreeUpdate implements TreeUpdate {

	private Collection<CacheTreeFileUpdate> treeFiles = new ArrayList<CacheTreeFileUpdate>();
	private CacheTreeFileUpdate actualTreeFile;
	private DirCache index;
	private int dirCacheEntryIndex = 0;
	private Collection<PathEdit> pathEdits = new ArrayList<PathEdit>();
	private GitRepository gitRepository;
	private IndexUpdate indexUpdate;

	public CacheTreeUpdate(Commit commit, GitRepository gitRepository,
			IndexUpdate indexUpdate) {
		this.gitRepository = gitRepository;
		this.indexUpdate = indexUpdate;
		try {
			index = resetRewriteRefDirCache(commit);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private DirCache resetRewriteRefDirCache(Commit commit)
			throws CorruptObjectException, IOException {
		Repository repo = gitRepository.getRepository();
		FS fs = repo.getFS();
		File indexLocation = repo.getIndexFile();
		DirCache dirCache = DirCache.lock(indexLocation, fs);

		RevCommit revCommit = commit.getRevCommit();
		indexUpdate.getTouchedCommits().add(revCommit);
		resetIndex(revCommit, dirCache);
		return dirCache;
	}

	private void resetIndex(RevCommit revCommit, DirCache dirCache)
			throws IOException {
		TreeWalk walk = null;
		DirCacheBuilder builder = dirCache.builder();
		Repository repo = gitRepository.getRepository();
		walk = new TreeWalk(repo);
		if (revCommit != null) {
			RevTree revTree = revCommit.getTree();
			walk.addTree(revTree);
		} else {
			walk.addTree(new EmptyTreeIterator());
		}
		walk.addTree(new DirCacheIterator(dirCache));
		walk.setRecursive(true);

		while (walk.next()) {
			AbstractTreeIterator cIter = walk.getTree(0,
					AbstractTreeIterator.class);
			if (cIter == null) {
				// Not in commit, don't add to new index
				continue;
			}

			final DirCacheEntry entry = new DirCacheEntry(walk.getRawPath());
			entry.setFileMode(cIter.getEntryFileMode());
			entry.setObjectIdFromRaw(cIter.idBuffer(), cIter.idOffset());

			DirCacheIterator dcIter = walk.getTree(1, DirCacheIterator.class);
			if (dcIter != null && dcIter.idEqual(cIter)) {
				DirCacheEntry indexEntry = dcIter.getDirCacheEntry();
				entry.setLastModified(indexEntry.getLastModified());
				entry.setLength(indexEntry.getLength());
			}

			builder.add(entry);
		}

		builder.finish();
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

	public void release() {
		index.unlock();
	}

	void registerPathEdit(DeletePath pathEdit) {
		pathEdits.add(pathEdit);
	}

}
