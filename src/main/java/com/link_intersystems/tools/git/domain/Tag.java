package com.link_intersystems.tools.git.domain;

public class Tag extends Ref {

	Tag(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

}
