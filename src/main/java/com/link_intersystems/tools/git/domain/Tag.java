package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

public class Tag extends Ref {

	Tag(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

	@Override
	public void clearReflog() throws IOException {
	}

	@Override
	public ObjectId getId() {
		org.eclipse.jgit.lib.Ref jgitRef = getJgitRef();
		ObjectId objectId = jgitRef.getPeeledObjectId();
		if (objectId == null) {
			objectId = jgitRef.getObjectId();
		}
		return objectId;
	}

}
