package com.link_intersystems.tools.git.domain;

import com.link_intersystems.tools.git.CommitRange;

public class CommitRangeTree extends TreeObject {

	private CommitRange commitRange;

	public CommitRangeTree(CommitRange commitRange) {
		super("");
		this.commitRange = commitRange;
	}

	public CommitRange getCommitRange() {
		return commitRange;
	}

}
