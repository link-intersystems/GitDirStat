package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.gitdirstat.domain.CommitRange;

public class CommitRangesRevWalkConfigurer implements RevWalkConfigurer {

	private Collection<CommitRange> commitRanges;

	public CommitRangesRevWalkConfigurer(Collection<CommitRange> commitRanges) {
		this.commitRanges = commitRanges;
	}

	@Override
	public void configure(RevWalk revWalk) throws IOException {
		for (CommitRange commitRange : commitRanges) {
			ObjectId fromInclusive = commitRange.getToInclusive();

			RevCommit revCommit = getRevCommit(revWalk, fromInclusive);
			if (revCommit != null) {
				revWalk.markStart(revCommit);
			}
		}
		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);
	}

	private RevCommit getRevCommit(RevWalk revWalk, ObjectId objectId)
			throws MissingObjectException, IOException {
		RevCommit revCommit = null;
		RevObject revObject = revWalk.parseAny(objectId);
		int type = revObject.getType();

		// 'git tag' [-a | -s | -u <key-id>] [-f] [-m <msg> | -F <file>]
		// <tagname> [<commit> | <object>]
		//
		// <commit> | <object>
		// The object that the new tag will refer to, usually a commit.
		// Defaults to HEAD.
		//
		if (Constants.OBJ_COMMIT == type) {
			revCommit = RevCommit.class.cast(revObject);
		}
		return revCommit;
	}
}
