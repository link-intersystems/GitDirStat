package com.link_intersystems.gitdirstat.domain.walk;

import org.eclipse.jgit.revwalk.RevWalk;

public class NullRevWalkConfigurer implements RevWalkConfigurer {

	public static final NullRevWalkConfigurer INSTANCE = new NullRevWalkConfigurer();

	@Override
	public void configure(RevWalk revWalk) {
	}

}
