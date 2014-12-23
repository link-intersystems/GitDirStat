package com.link_intersystems.gitdirstat.domain.walk;

import java.util.Collection;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;

import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.CommitRangeTree;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.ProgressListener;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectBuilderTreeEntryWalk;
import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry;

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

		RevWalkTemplate revWalkTemplate = new ProgressAwareRevWalkTemplate(
				gitRepository, progressListener);

		RevWalkConfigurer walkConfigurer = new CommitRangesRevWalkConfigurer(
				commitRanges);
		revWalkTemplate.setRevWalkConfigurer(walkConfigurer);
		ObjectReader objectReader = gitRepository.getObjectReader();
		TreeObjectBuilderTreeEntryWalk treeObjectBuilder = new TreeObjectBuilderTreeEntryWalk(
				root);
		TreeWalkTreeEntryWalkAdapter commitWalk = new TreeWalkTreeEntryWalkAdapter(
				objectReader, treeObjectBuilder);
		class TreeWalkFilter implements Predicate<TreeEntry> {

			Predicate<ObjectId> uniqueIds = UniquePredicate.uniquePredicate();
			Predicate<String> uniquePaths = UniquePredicate.uniquePredicate();

			@Override
			public boolean evaluate(TreeEntry treeEntry) {
				return !(uniqueIds.evaluate(treeEntry.getObjectId()) || uniquePaths
						.evaluate(treeEntry.getPathString()));
			}

		}

		commitWalk.setTreeWalkFilter(new TreeWalkFilter());

		try {
			revWalkTemplate.walk(commitWalk);
			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
