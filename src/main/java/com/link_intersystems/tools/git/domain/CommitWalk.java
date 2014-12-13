package com.link_intersystems.tools.git.domain;

import java.util.Iterator;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class CommitWalk implements Iterable<Commit> {

	private RevWalk revWalk;
	private CommitAccess commitAccess;

	public CommitWalk(RevWalk revWalk, CommitAccess commitAccess) {
		this.revWalk = revWalk;
		this.commitAccess = commitAccess;
	}

	@Override
	public Iterator<Commit> iterator() {
		return new CommitIterator(revWalk.iterator(), commitAccess);
	}

	private static class CommitIterator implements Iterator<Commit> {

		private Iterator<RevCommit> revCommitIterator;
		private CommitAccess commitAccess;

		public CommitIterator(Iterator<RevCommit> revCommitIterator,
				CommitAccess commitAccess) {
			this.revCommitIterator = revCommitIterator;
			this.commitAccess = commitAccess;
		}

		@Override
		public boolean hasNext() {
			return revCommitIterator.hasNext();
		}

		@Override
		public Commit next() {
			RevCommit revCommit = revCommitIterator.next();
			Commit commit = commitAccess.getCommit(revCommit);
			return commit;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
