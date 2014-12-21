package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.tools.git.CommitRange;

public class CommitWalkCommitRangeTreeBuilder implements CommitRangeTreeBuilder {

	private GitRepository gitRepository;

	public CommitWalkCommitRangeTreeBuilder(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	@Override
	public TreeObject build(Collection<CommitRange> commitRanges,
			ProgressListener progressListener) {
		String id = gitRepository.getId();
		try {
			CommitRangeTree root = new CommitRangeTree(id, commitRanges);
			if (commitRanges.isEmpty()) {
				return root;
			}

			int totalWork = getTotalWork(commitRanges);

			CommitWalker commitWalker = createCommitWalker(commitRanges);

			progressListener.start(totalWork);
			Predicate<Object> uniquePredicate = UniquePredicate
					.uniquePredicate();

			for (Commit commit : commitWalker) {
				if (progressListener.isCanceled()) {
					root = new CommitRangeTree(id, commitRanges);
					break;
				}

				TreeWalker treeWalker = commit.createTreeWalker();
				Iterator<TreeWalkerEntry> treeWalkerIterator = treeWalker
						.iterator();
				treeWalkerIterator = IteratorUtils.filteredIterator(
						treeWalkerIterator, UniquePredicate.uniquePredicate());

				while (treeWalkerIterator.hasNext()) {
					TreeWalkerEntry treeWalkerEntry = treeWalkerIterator.next();
					if (!uniquePredicate.evaluate(treeWalkerEntry)) {
						continue;
					}
					ObjectSize objectSize = treeWalkerEntry.getSize();
					String pathString = treeWalkerEntry.getPathString();
					TreeObject treeObject = root.makePath(pathString);
					treeObject.addObjectSize(objectSize);
				}

				treeWalker.release();
				progressListener.update(1);
			}
			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			progressListener.end();
		}
	}

	private int getTotalWork(Collection<CommitRange> commitRanges)
			throws IOException {
		RevWalk revWalk = createRevWalk(commitRanges);

		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);

		int total = 0;
		while (revWalk.next() != null) {
			total++;
		}
		return total;
	}

	private CommitWalker createCommitWalker(Collection<CommitRange> commitRanges)
			throws IOException {
		CommitWalker commitWalk = new CommitWalker(gitRepository);
		commitWalk.setCommitRanges(commitRanges);

		RevWalk revWalk = commitWalk.getRevWalk();
		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);

		return commitWalk;
	}

	private RevWalk createRevWalk(Collection<CommitRange> commitRanges)
			throws IOException {
		Repository repository = gitRepository.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		applyCommitRanges(revWalk, commitRanges);
		return revWalk;
	}

	private void applyCommitRanges(RevWalk revWalk,
			Collection<CommitRange> commitRanges)
			throws MissingObjectException, IncorrectObjectTypeException,
			IOException {
		for (CommitRange commitRange : commitRanges) {
			AnyObjectId fromInclusive = commitRange.getToInclusive();
			RevCommit revCommit = revWalk.parseCommit(fromInclusive);
			revWalk.markStart(revCommit);
		}
	}

}
