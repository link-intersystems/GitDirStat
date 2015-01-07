package com.link_intersystems.junit.jgit;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

class CommitSelectionRevFilter extends RevFilter {

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