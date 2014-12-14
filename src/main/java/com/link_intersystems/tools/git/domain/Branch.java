package com.link_intersystems.tools.git.domain;


public abstract class Branch extends Ref {

	Branch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}


}
