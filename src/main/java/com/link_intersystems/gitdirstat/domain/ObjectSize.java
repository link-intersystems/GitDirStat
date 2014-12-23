package com.link_intersystems.gitdirstat.domain;

import java.math.BigInteger;

import org.eclipse.jgit.lib.ObjectId;

public class ObjectSize {

	private ObjectId objectId;
	private BigInteger size;

	public ObjectSize(ObjectId objectId, long size) {
		this.objectId = objectId;
		this.size = BigInteger.valueOf(size);
	}

	public BigInteger getSize() {
		return size;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

}
