package com.link_intersystems.gitdirstat.domain;

import java.util.Iterator;

import com.link_intersystems.gitdirstat.domain.walk.CommitWalker;

public class RewriteIndexCommitWalkIterator implements Iterator<Commit> {

	private Iterator<Commit> commitWalk;

	public RewriteIndexCommitWalkIterator(CommitWalker commitWalk) {
		this.commitWalk = commitWalk.iterator();
	}

	@Override
	public boolean hasNext() {
		return commitWalk.hasNext();
	}

	@Override
	public Commit next() {
		Commit commit = commitWalk.next();
		return commit;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
