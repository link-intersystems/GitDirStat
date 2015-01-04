package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

public class CacheCommitUpdate implements CommitUpdate {

	private Commit commit;
	private GitRepository gitRepository;

	private PersonIdent authorUpdate;
	private PersonIdent committerUpdate;
	private String messageUpdate;
	private HistoryUpdate historyUpdate;
	private CacheTreeUpdate treeUpdate;
	private DirCache dirCache;

	CacheCommitUpdate(GitRepository gitRepository, Commit commit,
			HistoryUpdate historyUpdate, DirCache dirCache) {
		this.gitRepository = gitRepository;
		this.commit = commit;
		this.historyUpdate = historyUpdate;
		this.dirCache = dirCache;
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
		if (treeUpdate == null) {
			treeUpdate = new CacheTreeUpdate(dirCache);
		}
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
			if (treeUpdate != null) {
				treeUpdate.apply(dirCache);
			}

			// Write the index as tree to the object database. This may
			// fail for example when the index contains unmerged paths
			// (unresolved conflicts)
			ObjectId indexTreeId = dirCache.writeTree(odi);

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
		boolean isTreeUpdate = treeUpdate != null;
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

}
