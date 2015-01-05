package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ReflogEntry;

import com.link_intersystems.gitdirstat.domain.ExpireReflogCommand.ReflogEntryFilter;

public class HistoryUpdate {

	private class SkipReflogEntryFilter implements ReflogEntryFilter {

		private Set<ObjectId> expireCommits;

		public SkipReflogEntryFilter(Set<ObjectId> expireCommits) {
			this.expireCommits = expireCommits;
		}

		@Override
		public boolean accept(ReflogEntry reflogEntry) {
			ObjectId newId = reflogEntry.getNewId();
			ObjectId oldId = reflogEntry.getOldId();

			boolean oldIdExpired = !expireCommits.contains(oldId);
			boolean newIdExpired = !expireCommits.contains(newId);

			boolean noIdExpired = !oldIdExpired || !newIdExpired;
			return noIdExpired;
		}
	}

	Map<ObjectId, ObjectId> replacedCommits = new HashMap<ObjectId, ObjectId>();
	private GitRepository gitRepository;
	private IndexUpdate indexUpdate;

	public HistoryUpdate(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public void updateRefs() throws IOException, GitAPIException {
		List<Ref> allRefs = gitRepository.getRefs(Ref.class);
		for (Ref ref : allRefs) {
			if (ref.isUpdateable()) {
				nullSafeCommitUpdate(ref);
			}
		}
	}

	public void cleanupRepository() throws GitAPIException {
		Set<ObjectId> expireCommits = new HashSet<ObjectId>();
		expireCommits.addAll(replacedCommits.keySet());

		if (indexUpdate != null) {
			expireCommits = indexUpdate.getTouchedCommits();
		}

		ExpireReflogCommand expireReflogCommand = new ExpireReflogCommand(
				gitRepository);
		expireReflogCommand.setReflogEntryFilter(new SkipReflogEntryFilter(
				expireCommits));
		expireReflogCommand.setExpire(null);
		expireReflogCommand.call();

		gc();
	}

	private void gc() throws GitAPIException {
		Git git = gitRepository.getGit();
		GarbageCollectCommand gc = git.gc();
		gc.setExpire(null);
		gc.call();
	}

	private void nullSafeCommitUpdate(Ref ref) throws IOException {
		ObjectId objectId = ref.getCommitId();
		ObjectId rewrittenCommit = replacedCommits.get(objectId);
		if (rewrittenCommit != null) {
			ref.update(rewrittenCommit);
		}
	}

	public boolean hasReplacedParents(Commit commit) {
		ObjectId[] parentIds = commit.getParentIds();
		for (int i = 0; i < parentIds.length; i++) {
			ObjectId parentId = parentIds[i];
			if (replacedCommits.containsKey(parentId)) {
				return true;
			}
		}
		return false;
	}

	public void replaceCommit(Commit commit, ObjectId replacedCommitId) {
		replacedCommits.put(commit.getId(), replacedCommitId);
	}

	public ObjectId[] getParentIds(Commit commit) {
		ObjectId[] parentIds = commit.getParentIds();
		ObjectId[] replacedParentIds = new ObjectId[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			ObjectId parentId = parentIds[i];
			ObjectId replacedCommit = replacedCommits.get(parentId);
			if (replacedCommit == null) {
				replacedParentIds[i] = parentId;
			} else {
				replacedParentIds[i] = replacedCommit;
			}
		}
		return replacedParentIds;
	}

	public IndexUpdate begin() throws IOException, GitAPIException {
		indexUpdate = new IndexUpdate(gitRepository, this);
		return indexUpdate;
	}

	public void close() throws GitAPIException {
		Git git = gitRepository.getGit();
		git.reset().setMode(ResetType.HARD).call();

		if (indexUpdate != null) {
			indexUpdate.close();
		}
	}
}
