package com.link_intersystems.tools.git.domain;

import org.eclipse.jgit.lib.Ref;

public class RemoteBranch extends Branch {

	RemoteBranch(Ref jgitRef) {
		super(jgitRef);
	}

}
