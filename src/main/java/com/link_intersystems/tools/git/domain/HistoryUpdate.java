package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class HistoryUpdate {
	Map<String, Commit> replacedCommits = new HashMap<String, Commit>();
	private GitRepository gitRepository;

	public HistoryUpdate(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public void updateRefs() throws IOException {
		List<Ref> allRefs = gitRepository.getRefs(Ref.class);
		for (Ref ref : allRefs) {
			if (ref.isUpdateable()) {
				ObjectId objectId = ref.getCommitId();
				String objectName = objectId.name();
				if (replacedCommits.containsKey(objectName)) {
					Commit rewrittenCommit = replacedCommits.get(objectName);
					ref.update(rewrittenCommit.getId());
				}
			}
		}
	}

	public boolean hasReplacedParents(Commit commit) {
		ObjectId[] parentIds = commit.getParentIds();
		for (int i = 0; i < parentIds.length; i++) {
			ObjectId parentId = parentIds[i];
			String parentName = parentId.name();
			if (replacedCommits.containsKey(parentName)) {
				return true;
			}
		}
		return false;
	}

	public Commit replaceCommit(Commit commit, RevCommit revCommit) {
		CommitAccess commitAccess = gitRepository.getCommitAccess();
		Commit replacement = commitAccess.getCommit(revCommit);
		String commitName = commit.getId().name();
		replacedCommits.put(commitName, replacement);
		return replacement;
	}

	public ObjectId[] getParentIds(Commit commit) {
		ObjectId[] parentIds = commit.getParentIds();
		ObjectId[] replacedParentIds = new ObjectId[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			ObjectId parentId = parentIds[i];
			String parentName = parentId.name();
			Commit replacedCommit = replacedCommits.get(parentName);
			if (replacedCommit != null) {
				parentId = replacedCommit.getId();
			}
			replacedParentIds[i] = parentId;
		}
		return replacedParentIds;
	}

	public void gc() throws GitAPIException {
		Git git = gitRepository.getGit();
		GarbageCollectCommand gc = git.gc();
		gc.setExpire(null);
		gc.call();
	}
}
