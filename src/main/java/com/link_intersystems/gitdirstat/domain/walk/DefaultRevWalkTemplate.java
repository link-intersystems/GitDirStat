package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.gitdirstat.domain.GitRepository;

public class DefaultRevWalkTemplate extends AbstractRevWalkTemplate {

	public DefaultRevWalkTemplate(GitRepository gitRepository) {
		super(gitRepository);
	}

	@Override
	public void walk(RevCommitWalk revCommitWalk) throws IOException {
		RevWalk revWalk = createRevWalk();

		RevCommit revCommit = null;

		while ((revCommit = revWalk.next()) != null) {
			if (revCommitFilter.accept(revCommit)) {
				revCommitWalk.walk(revCommit);
			}
		}
	}
}
