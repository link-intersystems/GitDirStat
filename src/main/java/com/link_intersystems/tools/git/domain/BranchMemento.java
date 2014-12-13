package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

public class BranchMemento {

	private Git git;
	private String ref;
	private String branchName;

	public BranchMemento(Git git) {
		this.git = git;
	}

	public void save() throws IOException {
		Repository repository = git.getRepository();
		ref = repository.getFullBranch();
		branchName = repository.getBranch();
	}

	public void restore() throws GitAPIException {
		if (ref != null && branchName != null) {
			git.checkout().setName(branchName).setStartPoint(ref).call();
			git.reset().setMode(ResetType.HARD).call();
			git.clean().setCleanDirectories(true).setIgnore(true).call();
		}
	}

}
