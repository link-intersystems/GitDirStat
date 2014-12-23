package com.link_intersystems.gitdirstat.domain;


public abstract class Branch extends Ref {

	Branch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}


}
