package com.link_intersystems.junit.jgit;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.junit.Assert;

public class RepoAssertion {

	private Repository repository;

	public RepoAssertion(Repository repository) {
		this.repository = repository;
	}

	public ActualCommit getCommitAssertion(String revstr) throws Exception {
		ObjectId commitId = repository.resolve(revstr);
		Assert.assertNotNull("Commit " + revstr + " does not exist", commitId);
		RevWalk walk = new RevWalk(repository);
		RevCommit revCommit = walk.parseCommit(commitId);

		return new ActualCommit(revCommit);
	}

	public void assertThatAllCommits(CommitSelection commitSelection,
			CommitAssertion commitAssertion) throws Exception {

		RevWalk revWalk = new RevWalk(repository);
		revWalk.setRevFilter(new CommitSelectionRevFilter(commitSelection));
		revWalk.sort(RevSort.REVERSE);
		revWalk.sort(RevSort.TOPO, true);

		for (RevCommit revCommit : revWalk) {
			ActualCommit actualCommit = new ActualCommit(revCommit);
			commitAssertion.assertCommit(actualCommit);
		}
	}

	private static class CommitSelectionRevFilter extends RevFilter {

		private CommitSelection commitSelection;

		public CommitSelectionRevFilter(CommitSelection commitSelection) {
			this.commitSelection = commitSelection;
		}

		@Override
		public boolean include(RevWalk walker, RevCommit cmit)
				throws StopWalkException, MissingObjectException,
				IncorrectObjectTypeException, IOException {
			return commitSelection.accept(cmit);
		}

		@Override
		public RevFilter clone() {
			return this;
		}

	}
}
