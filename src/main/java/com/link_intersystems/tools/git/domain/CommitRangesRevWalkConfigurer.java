package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.tools.git.CommitRange;

public class CommitRangesRevWalkConfigurer implements RevWalkConfigurer {

	private Collection<CommitRange> commitRanges;

	public CommitRangesRevWalkConfigurer(Collection<CommitRange> commitRanges) {
		this.commitRanges = commitRanges;
	}

	@Override
	public void configure(RevWalk revWalk) throws IOException {
		for (CommitRange commitRange : commitRanges) {
			AnyObjectId fromInclusive = commitRange.getToInclusive();
			RevCommit revCommit = revWalk.parseCommit(fromInclusive);
			revWalk.markStart(revCommit);
		}
	}

}
