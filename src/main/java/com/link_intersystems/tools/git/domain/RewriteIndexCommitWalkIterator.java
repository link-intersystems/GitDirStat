package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class RewriteIndexCommitWalkIterator implements Iterator<Commit> {

	private Iterator<Commit> commitWalk;
	private Git git;
	String rewriteBranchName = "rewrite_branch";
	private Object rewriteBranch;

	public RewriteIndexCommitWalkIterator(Git git, CommitWalk commitWalk) {
		this.git = git;
		this.commitWalk = commitWalk.iterator();
	}

	@Override
	public boolean hasNext() {
		return commitWalk.hasNext();
	}

	@Override
	public Commit next() {
		Commit commit = commitWalk.next();
		readIndex(commit);
		return commit;
	}

	private void readIndex(Commit commit) {
		try {
			if (rewriteBranch == null) {
				Repository repository = git.getRepository();
				Ref ref = repository.getRef(rewriteBranchName);
				if (ref == null) {
					CheckoutCommand checkout = git.checkout();
					checkout.setStartPoint(commit.getRevCommit());
					checkout.setName(rewriteBranchName);
					checkout.setCreateBranch(true);
					rewriteBranch = checkout.call();
				} else {
					CheckoutCommand checkout = git.checkout();
					checkout.setForce(true);
					checkout.setName(rewriteBranchName);
					Ref call = checkout.call();
					rewriteBranch = ref;
				}

			} else {
				String resetObjectId = commit.getId().getName();
				ResetCommand reset = git.reset();
				reset.setMode(ResetType.MIXED);
				reset.setRef(resetObjectId);
				reset.call();
			}
		} catch (GitAPIException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void close() throws GitAPIException {
		if (rewriteBranch != null) {
			git.branchDelete().setBranchNames(rewriteBranchName).setForce(true)
					.call();
			rewriteBranch = null;
		}
	}
}
