package com.link_intersystems.gitdirstat.domain.walk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.Predicate;
import org.eclipse.jgit.lib.ObjectReader;

import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.CommitRangeTree;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.ProgressListener;
import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry;
import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry.TreeEntryEquality;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectBuilderTreeEntryWalk;

public class RevWalkCommitRangeTreeBuilder implements CommitRangeTreeBuilder {

	private GitRepository gitRepository;

	public RevWalkCommitRangeTreeBuilder(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	@Override
	public TreeObject build(Collection<CommitRange> commitRanges,
			ProgressListener progressListener) {
		String id = gitRepository.getId();
		CommitRangeTree root = new CommitRangeTree(id, commitRanges);
		if (commitRanges.isEmpty()) {
			return root;
		}

		ProgressAwareRevWalkTemplate revWalkTemplate = new ProgressAwareRevWalkTemplate(
				gitRepository, progressListener);

		RevWalkConfigurer walkConfigurer = new CommitRangesRevWalkConfigurer(
				commitRanges);
		revWalkTemplate.setRevWalkConfigurer(walkConfigurer);
		ObjectReader objectReader = gitRepository.getObjectReader();
		TreeObjectBuilderTreeEntryWalk treeObjectBuilder = new TreeObjectBuilderTreeEntryWalk(
				root);
		TreeWalkTreeEntryWalkAdapter commitWalk = new TreeWalkTreeEntryWalkAdapter(
				objectReader, treeObjectBuilder);

		commitWalk.setTreeWalkFilter(new UniqueTreeEntryFilter());

		try {
			revWalkTemplate.walk(commitWalk);
			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class UniqueTreeEntryFilter implements Predicate<TreeEntry> {

		private static final int _32K = 32768;

		Set<TreeEntryEquality> uniqueTreeEntries = new HashSet<TreeEntryEquality>(
				_32K, 0.5f);

		@Override
		public boolean evaluate(TreeEntry treeEntry) {
			return !uniqueTreeEntries.add(treeEntry.getEqualityObject());
		}

	}
}
