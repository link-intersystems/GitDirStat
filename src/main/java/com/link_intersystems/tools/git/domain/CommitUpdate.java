package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.ChangeIdUtil;

public class CommitUpdate {

	private Commit commit;
	private GitRepository gitRepository;

	private PersonIdent authorUpdate;
	private PersonIdent committerUpdate;
	private String messageUpdate;
	private HistoryUpdate historyUpdate;
	private TreeUpdate treeUpdate;

	CommitUpdate(GitRepository gitRepository, Commit commit,
			HistoryUpdate historyUpdate) {
		this.gitRepository = gitRepository;
		this.commit = commit;
		this.historyUpdate = historyUpdate;
	}

	public TreeUpdate getTreeUpdate(TreeFilter treeFilter) throws IOException {
		if (treeUpdate == null) {
			treeUpdate = new TreeUpdate(this, gitRepository, treeFilter);
		}
		return treeUpdate;
	}

	Commit getCommit() {
		return commit;
	}

	Commit execute() throws IOException {
		if (!mustUpdate()) {
			return null;
		}
		Repository repo = gitRepository.getRepository();
		RevWalk rw = new RevWalk(repo);
		DirCache index = repo.lockDirCache();
		try {
			ObjectInserter odi = repo.newObjectInserter();
			try {
				if (treeUpdate != null) {
					treeUpdate.apply(index);
				}

				// Write the index as tree to the object database. This may
				// fail for example when the index contains unmerged paths
				// (unresolved conflicts)
				ObjectId indexTreeId = index.writeTree(odi);

				insertChangeId(indexTreeId);

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

				RevCommit revCommit = rw.parseCommit(commitId);
				odi.flush();
				RefUpdate ru = repo.updateRef(Constants.HEAD);
				ru.setNewObjectId(commitId);
				ru.setForceUpdate(true);
				ru.update(rw);

				Commit replacedCommit = historyUpdate.replaceCommit(
						this.commit, revCommit);
				return replacedCommit;
			} finally {
				odi.release();
			}
		} finally {
			index.unlock();
		}

	}

	public String getMessage() {
		if (messageUpdate != null) {
			return messageUpdate;
		} else {
			return getOriginalMessage();
		}
	}

	public String getOriginalMessage() {
		return commit.getMessage();
	}

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

	public void setAuthor(String name, String email) {
		PersonIdent author = this.commit.getAuthor();
		Date origWhen = author.getWhen();
		TimeZone origTZ = author.getTimeZone();
		this.authorUpdate = new PersonIdent(name, email, origWhen, origTZ);
	}

	public PersonIdent getCommitter() {
		if (committerUpdate != null) {
			return committerUpdate;
		} else {
			return commit.getCommitter();
		}
	}

	public void setCommitter(String name, String email) {
		PersonIdent committer = this.commit.getCommitter();
		Date origWhen = committer.getWhen();
		TimeZone origTZ = committer.getTimeZone();
		this.committerUpdate = new PersonIdent(name, email, origWhen, origTZ);
		;
	}

	private void insertChangeId(ObjectId treeId) throws IOException {
		ObjectId firstParentId = null;
		RevCommit[] parents = commit.getRevCommitParents();

		if (parents.length > 0)
			firstParentId = parents[0].getId();

		String message = getMessage();
		PersonIdent committer = getCommitter();
		PersonIdent author = getAuthor();
		ObjectId changeId = ChangeIdUtil.computeChangeId(treeId, firstParentId,
				author, committer, message);
		message = ChangeIdUtil.insertId(message, changeId);
		if (changeId != null)
			message = message.replaceAll("\nChange-Id: I" //$NON-NLS-1$
					+ ObjectId.zeroId().getName() + "\n", "\nChange-Id: I" //$NON-NLS-1$ //$NON-NLS-2$
					+ changeId.getName() + "\n"); //$NON-NLS-1$
	}

	public TreeUpdate getTreeUpdate() throws IOException {
		return getTreeUpdate(TreeFilter.ALL);
	}
}
