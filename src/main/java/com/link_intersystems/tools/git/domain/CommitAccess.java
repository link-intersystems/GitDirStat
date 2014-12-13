package com.link_intersystems.tools.git.domain;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitAccess {

	Map<ObjectId, Commit> commitDatabase = new HashMap<ObjectId, Commit>();

	public Commit getCommit(RevCommit revCommit) {
		ObjectId id = revCommit.getId();
		Commit commit = commitDatabase.get(id);
		if (commit == null) {
			commit = new Commit(revCommit, this);
			commitDatabase.put(id, commit);
		}
		return commit;
	}

	public void delete(Commit commit) {
		ObjectId id = commit.getId();
		commitDatabase.remove(id);
	}

}
