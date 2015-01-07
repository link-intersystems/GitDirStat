package com.link_intersystems.junit.jgit;

import org.eclipse.jgit.revwalk.RevCommit;

public interface CommitSelection {

	boolean accept(RevCommit cmit);


}
