package com.link_intersystems.tools.git;

import org.eclipse.jgit.lib.AnyObjectId;

public class CommitRange {

	private AnyObjectId fromInclusive;
	private AnyObjectId toInclusive;

	public CommitRange(AnyObjectId fromInclusive, AnyObjectId toInclusive) {
		this.fromInclusive = fromInclusive;
		this.toInclusive = toInclusive;
	}

	public AnyObjectId getFromInclusive() {
		return fromInclusive;
	}

	public AnyObjectId getToInclusive() {
		return toInclusive;
	}

}
