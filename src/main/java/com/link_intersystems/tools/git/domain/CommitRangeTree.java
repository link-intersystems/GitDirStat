package com.link_intersystems.tools.git.domain;

import java.util.Collection;

import com.link_intersystems.tools.git.CommitRange;

public class CommitRangeTree extends TreeObject {

	private Collection<CommitRange> commitRanges;

	public CommitRangeTree(String repositoryId, Collection<CommitRange> commitRanges) {
		super(repositoryId);
		this.commitRanges = commitRanges;
	}

	public Collection<CommitRange> getCommitRange() {
		return commitRanges;
	}

}
