package com.link_intersystems.gitdirstat.domain.walk;

import org.eclipse.jgit.revwalk.RevCommit;

public interface RevCommitFilter {

	boolean accept(RevCommit revCommit);

}
