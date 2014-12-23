package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;

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
		ObjectId objectId = jgitRef.getPeeledObjectId();

		if (objectId == null) {
			objectId = jgitRef.getObjectId();
		}

		GitRepository gitRepository = getGitRepository();
		RevWalk revWalk = new RevWalk(gitRepository.getObjectReader());
		try {
			RevObject revObject = revWalk.parseAny(objectId);
			if (Constants.OBJ_COMMIT != revObject.getType()) {
				objectId = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return objectId;
	}

}
