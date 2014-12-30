package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import com.link_intersystems.gitdirstat.domain.ExpireReflogCommand.ReflogEntryFilter;

public class HistoryUpdate {

	private class SkipReflogEntryFilter implements ReflogEntryFilter {

		private Set<String> expireCommits;

		public SkipReflogEntryFilter(Set<String> expireCommits) {
			this.expireCommits = expireCommits;
		}

		@Override
		public boolean accept(ReflogEntry reflogEntry) {
			ObjectId newId = reflogEntry.getNewId();
			String newCommitId = newId.getName();

			ObjectId oldId = reflogEntry.getOldId();
			String oldCommitId = oldId.getName();
			boolean oldIdExpired = !expireCommits.contains(oldCommitId);
			boolean newIdExpired = !expireCommits.contains(newCommitId);
			boolean noIdExpired = !oldIdExpired || !newIdExpired;
			return noIdExpired;
		}
	}

	Map<String, RevCommit> replacedCommits = new HashMap<String, RevCommit>();
	private GitRepository gitRepository;
	String rewriteBranchName = "rewrite_branch";
	private RewriteBranch rewriteBranch;

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
		Set<String> expireCommits = new HashSet<String>();
		expireCommits.addAll(replacedCommits.keySet());

		if (rewriteBranch != null) {
			expireCommits = rewriteBranch.getTouchedCommits();
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
		if (objectId != null) {
			String objectName = objectId.name();
			if (replacedCommits.containsKey(objectName)) {
				RevCommit rewrittenCommit = replacedCommits.get(objectName);
				ref.update(rewrittenCommit.getId());
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

	public void replaceCommit(Commit commit, RevCommit revCommit) {
		String commitName = commit.getId().name();
		replacedCommits.put(commitName, revCommit);
	}

	public ObjectId[] getParentIds(Commit commit) {
		ObjectId[] parentIds = commit.getParentIds();
		ObjectId[] replacedParentIds = new ObjectId[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			ObjectId parentId = parentIds[i];
			String parentName = parentId.name();
			RevCommit replacedCommit = replacedCommits.get(parentName);
			if (replacedCommit != null) {
				parentId = replacedCommit.getId();
			}
			replacedParentIds[i] = parentId;
		}
		return replacedParentIds;
	}

	public RewriteBranch begin() throws IOException, GitAPIException {
		if (rewriteBranch != null) {
			throw new IllegalStateException("begin() can only be invoked once");
		}

		Repository repository = gitRepository.getRepository();
		Git git = gitRepository.getGit();
		org.eclipse.jgit.lib.Ref ref = repository.getRef(rewriteBranchName);
		if (ref == null) {
			CheckoutCommand checkout = git.checkout();
			checkout.setName(rewriteBranchName);
			checkout.setCreateBranch(true);
			org.eclipse.jgit.lib.Ref rewriteRef = checkout.call();
			rewriteBranch = new RewriteBranch(rewriteRef, gitRepository, this);
			return rewriteBranch;
		} else {
			throw new RewriteBranchExistsException(
					"Can not begin history rewrite. Branch "
							+ rewriteBranchName + " already exists",
					ref.getName());
		}
	}

	public void close() throws GitAPIException {
		if (rewriteBranch != null) {
			rewriteBranch.close();
		}
	}
}
