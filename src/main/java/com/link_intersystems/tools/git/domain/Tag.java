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
	public ObjectId getCommitId() {
		org.eclipse.jgit.lib.Ref jgitRef = getJgitRef();
		ObjectId commitId = jgitRef.getPeeledObjectId();
		if (commitId == null) {
			commitId = jgitRef.getObjectId();
		}
		return commitId;
	}

}
