package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

public class IndexUpdate {
	String rewriteBranchName = "rewrite_branch";
	private HistoryUpdate historyUpdate;
	private GitRepository gitRepository;
	private CacheCommitUpdate cacheCommitUpdate;

	private Set<ObjectId> touchedCommits = new HashSet<ObjectId>();
	private DirCache dirCache;
	private CachingObjectReader cachingObjectReader;

	IndexUpdate(GitRepository gitRepository, HistoryUpdate historyUpdate) {
		this.gitRepository = gitRepository;
		this.historyUpdate = historyUpdate;
		ObjectReader objectReader = gitRepository.getObjectReader();
		cachingObjectReader = new CachingObjectReader(objectReader);
	}

	public CacheCommitUpdate beginUpdate(Commit commit) throws GitAPIException,
			IOException {
		cacheCommitUpdate = new CacheCommitUpdate(gitRepository, commit,
				historyUpdate, this);
		return cacheCommitUpdate;
	}

	public void close() throws GitAPIException {
		cachingObjectReader.release();
		if (dirCache != null) {
			dirCache.unlock();
		}
	}

	Set<ObjectId> getTouchedCommits() {
		return touchedCommits;
	}

	public DirCache getDirCache() {
		if (dirCache == null) {
			dirCache = DirCache.newInCore();
		}
		return dirCache;

	}

	public DirCache resetDirCache(Commit commit) throws IOException {
		DirCache dirCache = getDirCache();
		RevCommit revCommit = commit.getRevCommit();
		touchedCommits.add(revCommit);
		resetIndex(revCommit, dirCache);
		return dirCache;
	}

	private void resetIndex(RevCommit revCommit, DirCache dirCache)
			throws IOException {
		TreeWalk walk = new TreeWalk(cachingObjectReader);
		walk.setRecursive(true);

		RevTree revTree = revCommit.getTree();
		walk.addTree(revTree);

		DirCacheBuilder builder = dirCache.builder();
		walk.addTree(new DirCacheIterator(dirCache));

		buildDirCache(walk, builder);

		builder.finish();
	}

	private void buildDirCache(TreeWalk walk, DirCacheBuilder builder)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
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
	}

	private static class CachingObjectReader extends ObjectReader {

		private Map<ObjectId, ObjectLoader> objectLoaderCache = new HashMap<ObjectId, ObjectLoader>();
		private ObjectReader delegate;

		public CachingObjectReader(ObjectReader delegate) {
			this.delegate = delegate;
		}

		private CachingObjectReader(Map<ObjectId, ObjectLoader> rawTreeCache,
				ObjectReader delegate) {
			this.objectLoaderCache = rawTreeCache;
			this.delegate = delegate;
		}

		@Override
		public ObjectReader newReader() {
			return new CachingObjectReader(objectLoaderCache, delegate);
		}

		@Override
		public Collection<ObjectId> resolve(AbbreviatedObjectId id)
				throws IOException {
			return delegate.resolve(id);
		}

		@Override
		public ObjectLoader open(AnyObjectId objectId, int typeHint)
				throws MissingObjectException, IncorrectObjectTypeException,
				IOException {
			ObjectLoader objectLoader = objectLoaderCache.get(objectId);
			if (objectLoader == null) {
				objectLoader = delegate.open(objectId, Constants.OBJ_TREE);
				objectLoaderCache.put(objectId.copy(), objectLoader);
			}
			return objectLoader;
		}

		@Override
		public Set<ObjectId> getShallowCommits() throws IOException {
			return delegate.getShallowCommits();
		}

		@Override
		public void release() {
			super.release();
			objectLoaderCache.clear();
		}
	}
}
