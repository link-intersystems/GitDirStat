package com.link_intersystems.gitdirstat.domain.walk;

import org.eclipse.jgit.revwalk.RevCommit;

public class NullRevCommitFilter implements RevCommitFilter {

	public static final NullRevCommitFilter INSTANCE = new NullRevCommitFilter();

	@Override
	public boolean accept(RevCommit revCommit) {
		return true;
	}

}
