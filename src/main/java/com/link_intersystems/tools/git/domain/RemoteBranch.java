package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

public class RemoteBranch extends Branch {

	RemoteBranch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

	@Override
	public boolean isUpdateable() {
		return false;
	}

	public void clearReflog() throws IOException {
	}

	public LocalBranch getLocalBranch() throws IOException {
		LocalBranch localBranchForRemote = null;
		GitRepository gitRepository = getGitRepository();
		Repository repo = gitRepository.getRepository();
		List<LocalBranch> localRefs = gitRepository.getRefs(LocalBranch.class);
		for (LocalBranch localBranch : localRefs) {
			BranchTrackingStatus branchTrackingStatus = BranchTrackingStatus
					.of(repo, localBranch.getName());
			if (branchTrackingStatus == null) {
				continue;
			}

			String remoteTrackingBranch = branchTrackingStatus
					.getRemoteTrackingBranch();
			if (getName().equals(remoteTrackingBranch)) {
				localBranchForRemote = localBranch;
				break;
			}
		}
		return localBranchForRemote;
	}

	public LocalBranch createLocalBranch() throws GitAPIException {
		GitRepository gitRepository = getGitRepository();
		Repository repository = gitRepository.getRepository();
		Git git = gitRepository.getGit();
		CreateBranchCommand branchCreate = git.branchCreate();
		String name = getName();
		String shortenRefName = repository.shortenRemoteBranchName(name);
		branchCreate.setName(shortenRefName);
		branchCreate.setStartPoint(name);
		branchCreate.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM);
		Ref call = branchCreate.call();
		return new LocalBranch(gitRepository, call);

	}
}
