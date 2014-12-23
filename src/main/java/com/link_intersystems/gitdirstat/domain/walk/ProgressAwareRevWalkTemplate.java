package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.ProgressListener;

public class ProgressAwareRevWalkTemplate extends AbstractRevWalkTemplate {

	private ProgressListener progressListener;

	public ProgressAwareRevWalkTemplate(GitRepository gitRepository,
			ProgressListener progressListener) {
		super(gitRepository);
		this.progressListener = progressListener;
	}

	@Override
	public void walk(RevCommitWalk revCommitWalk) throws IOException {
		RevWalk revWalk = createRevWalk();
		int totalWork = countRevCommits(revWalk);
		progressListener.start(totalWork);
		try {
			revWalk = createRevWalk();
			RevCommit revCommit = null;

			while ((revCommit = revWalk.next()) != null) {
				if (revCommitFilter.accept(revCommit)) {
					revCommitWalk.walk(revCommit);
				}
				progressListener.update(1);
			}

		} finally {
			progressListener.end();
		}
	}

	private int countRevCommits(RevWalk revWalk) throws IOException {
		int total = 0;
		while (revWalk.next() != null) {
			total++;
		}
		return total;
	}

}
