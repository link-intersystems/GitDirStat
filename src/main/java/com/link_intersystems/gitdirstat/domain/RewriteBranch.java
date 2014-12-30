package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;

import com.link_intersystems.lang.Assert;

public class RewriteBranch {

	private org.eclipse.jgit.lib.Ref rewriteBranch;
	private Git git;
	private HistoryUpdate historyUpdate;
	private GitRepository gitRepository;
	private CacheCommitUpdate latestCommitUpdate;

	private Set<String> touchedCommits = new HashSet<String>();

	RewriteBranch(org.eclipse.jgit.lib.Ref rewriteBranch,
			GitRepository gitRepository, HistoryUpdate historyUpdate) {
		Assert.notNull("rewriteBranch", rewriteBranch);
		this.rewriteBranch = rewriteBranch;
		this.gitRepository = gitRepository;
		this.git = gitRepository.getGit();
		this.historyUpdate = historyUpdate;
	}

	public CacheCommitUpdate beginUpdate(Commit commit) throws GitAPIException,
			IOException {
		if (rewriteBranch == null) {
			throw new IllegalStateException("RewriteBranch already closed");
		}

		String resetCommitId = commit.getId().getName();
		ResetCommand reset = git.reset();
		reset.setMode(ResetType.MIXED);
		reset.setRef(resetCommitId);
		reset.call();

		touchedCommits.add(resetCommitId);

		if (latestCommitUpdate != null) {
			latestCommitUpdate.end();
		}

		Repository repo = gitRepository.getRepository();
		DirCache dirCache = repo.lockDirCache();

		latestCommitUpdate = new CacheCommitUpdate(gitRepository, commit,
				historyUpdate, dirCache);
		return latestCommitUpdate;
	}

	public void close() throws GitAPIException {
		if (latestCommitUpdate != null) {
			latestCommitUpdate.end();
		}

		git.branchDelete().setBranchNames(rewriteBranch.getName())
				.setForce(true).call();
		rewriteBranch = null;
	}

	Set<String> getTouchedCommits() {
		return touchedCommits;
	}
}