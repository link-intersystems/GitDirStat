package com.link_intersystems.tools.git.domain;

import org.eclipse.jgit.revwalk.RevCommit;

public interface RevCommitFilter {

	boolean accept(RevCommit revCommit);

}
