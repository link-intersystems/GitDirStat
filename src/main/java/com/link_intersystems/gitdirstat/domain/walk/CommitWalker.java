package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.gitdirstat.domain.Commit;
import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.GitRepository;

public class CommitWalker implements Iterable<Commit> {

	private RevWalk revWalk;
	private GitRepository gitRepository;

	public CommitWalker(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public RevWalk getRevWalk() {
		if (revWalk == null) {
			Repository repo = gitRepository.getRepository();
			revWalk = new RevWalk(repo);
		}

		return revWalk;
	}

	@Override
	public Iterator<Commit> iterator() {
		return new CommitIterator(revWalk.iterator(), gitRepository);
	}

	private static class CommitIterator implements Iterator<Commit> {

		private Iterator<RevCommit> revCommitIterator;
		private GitRepository gitRepository;
		private Predicate<Object> uniquePredicate;

		public CommitIterator(Iterator<RevCommit> revCommitIterator,
				GitRepository gitRepository) {
			this.revCommitIterator = revCommitIterator;
			this.gitRepository = gitRepository;
			uniquePredicate = UniquePredicate.uniquePredicate();
		}

		@Override
		public boolean hasNext() {
			return revCommitIterator.hasNext();
		}

		@Override
		public Commit next() {
			RevCommit revCommit = revCommitIterator.next();
			if (!uniquePredicate.evaluate(revCommit)) {
				System.out.println(revCommit);
			}
			Commit commit = gitRepository.getCommit(revCommit);
			return commit;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public void setCommitRanges(Collection<CommitRange> commitRanges)
			throws IOException {
		RevWalk revWalk = getRevWalk();
		for (CommitRange commitRange : commitRanges) {
			AnyObjectId fromInclusive = commitRange.getToInclusive();
			RevCommit revCommit = revWalk.parseCommit(fromInclusive);
			revWalk.markStart(revCommit);
		}
	}

	public void sort(RevSort... revSorts) {
		for (int i = 0; i < revSorts.length; i++) {
			RevSort revSort = revSorts[i];
			if (i == 0) {
				revWalk.sort(revSort);
			} else {
				revWalk.sort(revSort, true);
			}
		}
	}

	public int getWalkCount() {
		Iterator<Commit> iterator = iterator();
		int total = 0;
		while (iterator.hasNext()) {
			total++;
			iterator.next();
		}
		return total;
	}

}
