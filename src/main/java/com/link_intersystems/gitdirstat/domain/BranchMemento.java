package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class BranchMemento {

	private Git git;
	private String branchName;

	public BranchMemento(Git git) {
		this.git = git;
	}

	public void save() throws IOException {
		Repository repository = git.getRepository();
		branchName = repository.getBranch();
	}

	public void restore() throws GitAPIException {
		if (branchName != null) {
			git.reset().setMode(ResetType.HARD).call();
			git.clean().setCleanDirectories(true).setIgnore(true).call();
			git.checkout().setName(branchName).call();
		}
	}

}
