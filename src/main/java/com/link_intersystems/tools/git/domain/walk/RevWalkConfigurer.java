package com.link_intersystems.tools.git.domain.walk;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevWalk;

public interface RevWalkConfigurer {

	public void configure(RevWalk revWalk) throws IOException;
}
