package com.link_intersystems.gitdirstat.domain;

import org.eclipse.jgit.lib.ObjectId;

public class CommitRange {

	private ObjectId fromInclusive;
	private ObjectId toInclusive;

	public CommitRange(ObjectId fromInclusive, ObjectId toInclusive) {
		this.fromInclusive = fromInclusive;
		this.toInclusive = toInclusive;
	}

	public ObjectId getFromInclusive() {
		return fromInclusive;
	}

	public ObjectId getToInclusive() {
		return toInclusive;
	}

}
