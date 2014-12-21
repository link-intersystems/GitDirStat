package com.link_intersystems.tools.git.domain.walk;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.ProgressListener;

public class ProgressAwareRevWalkTemplate extends AbstractRevWalkTemplate {

	private ProgressListener progressListener;

	public ProgressAwareRevWalkTemplate(GitRepository gitRepository,
			ProgressListener progressListener) {
		super(gitRepository);
		this.progressListener = progressListener;
	}

	@Override
	public void walk(RevCommitWalk revCommitWalk) throws IOException {
		int totalWork = getTotalWork();
		progressListener.start(totalWork);
		try {
			RevWalk revWalk = createRevWalk();
			RevCommit revCommit = null;

			while ((revCommit = revWalk.next()) != null) {
//				if (revCommitFilter.accept(revCommit)) {
					revCommitWalk.walk(revCommit);
//				}
				progressListener.update(1);
			}

		} finally {
			progressListener.end();
		}
	}

	private int getTotalWork() throws IOException {
		RevWalk revWalk = createRevWalk();

		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);

		int total = 0;
		while (revWalk.next() != null) {
			total++;
		}

		return total;
	}

}
