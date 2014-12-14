package com.link_intersystems.tools.git.domain;

import java.io.IOException;

public class Tag extends Ref {

	Tag(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

	@Override
	public void clearReflog() throws IOException {
	}

}
