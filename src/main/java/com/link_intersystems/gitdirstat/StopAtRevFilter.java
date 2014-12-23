package com.link_intersystems.gitdirstat;

import java.io.IOException;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

public class StopAtRevFilter extends RevFilter {

	private AnyObjectId stopAtCommitExclusive;
	private boolean stop;

	public StopAtRevFilter(AnyObjectId stopAtCommitExclusive) {
		this.stopAtCommitExclusive = stopAtCommitExclusive;
	}

	@Override
	public RevFilter clone() {
		return new StopAtRevFilter(stopAtCommitExclusive);
	}

	@Override
	public boolean include(RevWalk revWalk, RevCommit revCommit)
			throws StopWalkException, MissingObjectException,
			IncorrectObjectTypeException, IOException {
		if (stop) {
			throw StopWalkException.INSTANCE;
		}
		if (revCommit.equals(stopAtCommitExclusive)) {
			stop = true;
		}
		return !stop;
	}

}
