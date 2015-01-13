package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.gitdirstat.domain.Commit;
import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.GitRepository;

public class CommitWalker implements Iterable<Commit> {

	private GitRepository gitRepository;
	private Collection<CommitRange> commitRanges = new LinkedHashSet<CommitRange>();
	private RevSort[] revSorts = new RevSort[0];
	private Collection<CommitIterator> iterators = new ArrayList<CommitIterator>();

	public CommitWalker(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	private RevWalk createRevWalk() {
		ObjectReader objectReader = gitRepository.getObjectReader();
		RevWalk revWalk = new RevWalk(objectReader);

		applyCommitRanges(revWalk);
		applyRevSorts(revWalk);

		return revWalk;
	}

	private void applyCommitRanges(RevWalk revWalk) {
		try {
			for (CommitRange commitRange : commitRanges) {
				AnyObjectId fromInclusive = commitRange.getToInclusive();
				RevCommit revCommit;
				revCommit = revWalk.parseCommit(fromInclusive);
				revWalk.markStart(revCommit);
			}
		} catch (IOException e) {
			throw new RuntimeException("Can't create RevWalk instance", e);
		}
	}

	private void applyRevSorts(RevWalk revWalk) {
		for (int i = 0; i < revSorts.length; i++) {
			RevSort revSort = revSorts[i];
			if (i == 0) {
				revWalk.sort(revSort);
			} else {
				revWalk.sort(revSort, true);
			}
		}
	}

	@Override
	public Iterator<Commit> iterator() {
		RevWalk revWalk = createRevWalk();
		CommitIterator commitIterator = new CommitIterator(revWalk,
				gitRepository);
		iterators.add(commitIterator);
		return commitIterator;
	}

	public void close() {
		for (CommitIterator commitIterator : iterators) {
			commitIterator.close();
		}
	}

	private static class CommitIterator implements Iterator<Commit> {

		private Iterator<RevCommit> revCommitIterator;
		private GitRepository gitRepository;
		private Predicate<Object> uniquePredicate;
		private RevWalk revWalk;

		public CommitIterator(RevWalk revWalk, GitRepository gitRepository) {
			this.revWalk = revWalk;
			this.revCommitIterator = revWalk.iterator();
			this.gitRepository = gitRepository;
			uniquePredicate = UniquePredicate.uniquePredicate();
		}

		@Override
		public boolean hasNext() {
			boolean hasNext = revCommitIterator.hasNext();
			if (!hasNext) {
				close();
			}
			return hasNext;
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

		public void close() {
			revWalk.release();
		}

	}

	public void setCommitRanges(Collection<CommitRange> commitRanges)
			throws IOException {
		this.commitRanges.clear();
		this.commitRanges.addAll(commitRanges);
	}

	public void sort(RevSort... revSorts) {
		if (revSorts == null) {
			revSorts = new RevSort[0];
		}
		this.revSorts = revSorts;
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
