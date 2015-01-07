package com.link_intersystems.gitdirstat;

import com.link_intersystems.junit.jgit.ActualCommit;
import com.link_intersystems.junit.jgit.ActualTree;

public interface CommitTreeAssertion {

	void assertTree(ActualCommit actualCommit, ActualTree actualTree);

}
