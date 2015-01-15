package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;

public class CacheCommitUpdate implements CommitUpdate {

	private Commit commit;
	private GitRepository gitRepository;

	private PersonIdent authorUpdate;
	private PersonIdent committerUpdate;
	private String messageUpdate;
	private HistoryUpdate historyUpdate;
	private CacheTreeUpdate treeUpdate;
	private DirCache dirCache;
	private IndexUpdate indexUpdate;

	CacheCommitUpdate(GitRepository gitRepository, Commit commit,
			HistoryUpdate historyUpdate, IndexUpdate indexUpdate) {
		this.gitRepository = gitRepository;
		this.commit = commit;
		this.historyUpdate = historyUpdate;
		this.indexUpdate = indexUpdate;
		try {
			this.dirCache = resetRewriteRefDirCache(commit);
			treeUpdate = new CacheTreeUpdate(dirCache);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private DirCache resetRewriteRefDirCache(Commit commit)
			throws CorruptObjectException, IOException {
		Repository repo = gitRepository.getRepository();
		DirCache dirCache = repo.lockDirCache();

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

	public void end() {
		dirCache.unlock();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.link_intersystems.tools.git.domain.CommitUpdate#getTreeUpdate(org
	 * .eclipse.jgit.treewalk.filter.TreeFilter)
	 */
	@Override
	public TreeUpdate getTreeUpdate() throws IOException {
		return treeUpdate;
	}

	Commit getCommit() {
		return commit;
	}

	void writeCommit() throws IOException {
		if (!mustUpdate()) {
			return;
		}
		Repository repo = gitRepository.getRepository();
		ObjectInserter odi = repo.newObjectInserter();
		try {
			ObjectId indexTreeId = null;
			if (treeUpdate.apply(dirCache)) {
				// Write the index as tree to the object database. This may
				// fail for example when the index contains unmerged paths
				// (unresolved conflicts)
				indexTreeId = dirCache.writeTree(odi);
			} else {
				RevCommit revCommit = commit.getRevCommit();
				indexTreeId = revCommit.getTree();
			}

			// Create a Commit object, populate it and write it
			CommitBuilder commit = new CommitBuilder();
			commit.setCommitter(getCommitter());
			commit.setAuthor(getAuthor());
			commit.setMessage(getMessage());

			ObjectId[] parentIds = historyUpdate.getParentIds(getCommit());
			commit.setParentIds(parentIds);
			commit.setTreeId(indexTreeId);
			ObjectId commitId = odi.insert(commit);
			odi.flush();

			historyUpdate.replaceCommit(this.commit, commitId);
		} finally {
			odi.release();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.CommitUpdate#getMessage()
	 */
	@Override
	public String getMessage() {
		if (messageUpdate != null) {
			return messageUpdate;
		} else {
			return getOriginalMessage();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.link_intersystems.tools.git.domain.CommitUpdate#getOriginalMessage()
	 */
	@Override
	public String getOriginalMessage() {
		return commit.getMessage();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.CommitUpdate#getAuthor()
	 */
	@Override
	public PersonIdent getAuthor() {
		if (authorUpdate != null) {
			return authorUpdate;
		} else {
			return commit.getAuthor();
		}
	}

	private boolean mustUpdate() {
		boolean attributesChanges = authorUpdate != null
				|| committerUpdate != null || messageUpdate != null;
		boolean rewrittenCommit = historyUpdate.hasReplacedParents(commit);
		boolean isTreeUpdate = treeUpdate.hasUpdates();
		return attributesChanges || rewrittenCommit || isTreeUpdate;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.link_intersystems.tools.git.domain.CommitUpdate#setAuthor(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public void setAuthor(String name, String email) {
		PersonIdent author = this.commit.getAuthor();
		Date origWhen = author.getWhen();
		TimeZone origTZ = author.getTimeZone();
		this.authorUpdate = new PersonIdent(name, email, origWhen, origTZ);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.CommitUpdate#getCommitter()
	 */
	@Override
	public PersonIdent getCommitter() {
		if (committerUpdate != null) {
			return committerUpdate;
		} else {
			return commit.getCommitter();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.link_intersystems.tools.git.domain.CommitUpdate#setCommitter(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void setCommitter(String name, String email) {
		PersonIdent committer = this.commit.getCommitter();
		Date origWhen = committer.getWhen();
		TimeZone origTZ = committer.getTimeZone();
		this.committerUpdate = new PersonIdent(name, email, origWhen, origTZ);
	}

	@Override
	public String getId() {
		return commit.getId().name();
	}

	@Override
	public String getAbbreviatedId() {
		return commit.getId().abbreviate(7).name();
	}

}
