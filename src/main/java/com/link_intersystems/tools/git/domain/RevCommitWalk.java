package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;

public interface RevCommitWalk {

	public void walk(RevCommit revCommit) throws IOException;
}
