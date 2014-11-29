package com.link_intersystems.tools.git.domain;

import com.link_intersystems.tools.git.CommitRange;

public class CommitRangeTree extends TreeObject {

	private CommitRange commitRange;

	public CommitRangeTree(String repositoryId, CommitRange commitRange) {
		super(repositoryId);
		this.commitRange = commitRange;
	}

	public CommitRange getCommitRange() {
		return commitRange;
	}

}
