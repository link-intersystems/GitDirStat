package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

public class IndexUpdate {
	String rewriteBranchName = "rewrite_branch";
	private HistoryUpdate historyUpdate;
	private GitRepository gitRepository;
	private CacheCommitUpdate latestCommitUpdate;

	private Set<ObjectId> touchedCommits = new HashSet<ObjectId>();

	IndexUpdate(GitRepository gitRepository, HistoryUpdate historyUpdate) {
		this.gitRepository = gitRepository;
		this.historyUpdate = historyUpdate;
	}

	public CacheCommitUpdate beginUpdate(Commit commit) throws GitAPIException,
			IOException {
		if (latestCommitUpdate != null) {
			latestCommitUpdate.end();
		}

		DirCache dirCache = resetRewriteRefDirCache(commit);

		latestCommitUpdate = new CacheCommitUpdate(gitRepository, commit,
				historyUpdate, dirCache);
		return latestCommitUpdate;
	}

	private DirCache resetRewriteRefDirCache(Commit commit)
			throws CorruptObjectException, IOException {
		Repository repo = gitRepository.getRepository();
		DirCache dirCache = repo.lockDirCache();

		RevCommit revCommit = commit.getRevCommit();
		touchedCommits.add(revCommit);
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

	public void close() throws GitAPIException {
		if (latestCommitUpdate != null) {
			latestCommitUpdate.end();
		}
	}

	Set<ObjectId> getTouchedCommits() {
		return touchedCommits;
	}

}
