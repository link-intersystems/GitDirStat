package com.link_intersystems.gitdirstat.domain;

import java.util.Collection;

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
