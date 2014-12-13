package com.link_intersystems.tools.git.domain;

public class SymbolicRef extends Ref {

	public SymbolicRef(GitRepository gitRepository,
			org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

}
