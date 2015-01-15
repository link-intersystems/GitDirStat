package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;

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


		latestCommitUpdate = new CacheCommitUpdate(gitRepository, commit,
				historyUpdate, this);
		return latestCommitUpdate;
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
