package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

public class Commit {

	private RevCommit revCommit;
	private Commit[] parents;

	private GitRepository gitRepository;

	Commit(RevCommit revCommit, GitRepository gitRepository) {
		this.gitRepository = gitRepository;
		this.revCommit = revCommit;
	}

	public ObjectId getId() {
		return revCommit.getId();
	}

	public PersonIdent getCommitter() {
		return revCommit.getCommitterIdent();
	}

	public PersonIdent getAuthor() {
		return revCommit.getAuthorIdent();
	}

	public String getMessage() {
		return revCommit.getFullMessage();
	}

	public ObjectId[] getParentIds() {
		Commit[] commitParents = getCommitParents();
		ObjectId[] parentIds = new ObjectId[commitParents.length];
		for (int i = 0; i < commitParents.length; i++) {
			parentIds[i] = commitParents[i].getId();
		}
		return parentIds;
	}

	RevCommit getRevCommit() {
		return revCommit;
	}

	public RevCommit[] getRevCommitParents() {
		Commit[] parentCommits = getCommitParents();
		RevCommit[] parents = new RevCommit[parentCommits.length];
		for (int i = 0; i < this.parents.length; i++) {
			parents[i] = parentCommits[i].getRevCommit();
		}
		return parents;
	}

	private Commit[] getCommitParents() {
		if (parents == null) {
			RevCommit[] revParents = revCommit.getParents();
			parents = new Commit[revParents.length];
			for (int i = 0; i < revParents.length; i++) {
				RevCommit revParent = revParents[i];
				parents[i] = gitRepository.getCommit(revParent);
			}
		}
		return parents;
	}

	public TreeWalker createTreeWalker() throws IOException {
		return new TreeWalker(this);
	}

	public GitRepository getGitRepository() {
		return gitRepository;
	}

}
