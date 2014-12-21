package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class FilterCondition {

	private GitRepository gitRepository;
	private List<RemoteBranch> refs;

	public FilterCondition(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public void assertPrecondition() throws IOException, GitAPIException {
		Git git = gitRepository.getGit();
		Repository repository = git.getRepository();
		if (repository.isBare()) {
			return;
		}

		refs = gitRepository.getRefs(RemoteBranch.class);
		for (RemoteBranch remoteBranch : refs) {
			LocalBranch localBranch = remoteBranch.getLocalBranch();
			if (localBranch == null) {
				localBranch = remoteBranch.createLocalBranch();
			} else if (!localBranch.isUpToDate()) {
				throw new GitAPIException("Local branch of remote branch "
						+ remoteBranch + " contains changes.") {

					/**
							 *
							 */
					private static final long serialVersionUID = -7544517884597872780L;
				};
			}
		}

	}

}
