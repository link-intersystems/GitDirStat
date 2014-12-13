package com.link_intersystems.tools.git.domain;

public class RemoteBranch extends Branch {

	RemoteBranch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

	@Override
	public boolean isUpdateable() {
		return false;
	}

}
