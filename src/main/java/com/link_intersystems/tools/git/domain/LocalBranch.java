package com.link_intersystems.tools.git.domain;

import org.eclipse.jgit.lib.Ref;


public class LocalBranch extends Branch {

	LocalBranch(Ref jgitRef) {
		super(jgitRef);
	}

}
