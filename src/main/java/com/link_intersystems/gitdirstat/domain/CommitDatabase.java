package com.link_intersystems.gitdirstat.domain;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class CommitDatabase {

	Map<ObjectId, Commit> commitDatabase = new HashMap<ObjectId, Commit>();
	private GitRepository gitRepository;

	CommitDatabase(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public Commit getCommit(RevCommit revCommit) {
		ObjectId id = revCommit.getId();
		Commit commit = commitDatabase.get(id);
		if (commit == null) {
			commit = new Commit(revCommit, gitRepository);
			commitDatabase.put(id, commit);
		}
		return commit;
	}

	public void delete(Commit commit) {
		ObjectId id = commit.getId();
		commitDatabase.remove(id);
	}

}
