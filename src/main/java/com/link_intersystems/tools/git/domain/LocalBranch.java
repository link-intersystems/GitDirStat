package com.link_intersystems.tools.git.domain;


public class LocalBranch extends Branch {

	LocalBranch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

}
