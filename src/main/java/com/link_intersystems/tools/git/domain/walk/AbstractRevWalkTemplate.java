package com.link_intersystems.tools.git.domain.walk;

import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import com.link_intersystems.tools.git.domain.GitRepository;

public abstract class AbstractRevWalkTemplate implements RevWalkTemplate {

	protected GitRepository gitRepository;
	private RevWalkConfigurer revWalkConfigurer = NullRevWalkConfigurer.INSTANCE;
	protected RevCommitFilter revCommitFilter = NullRevCommitFilter.INSTANCE;

	public AbstractRevWalkTemplate(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	@Override
	public void setRevWalkConfigurer(RevWalkConfigurer revWalkConfigurer) {
		if (revWalkConfigurer == null) {
			revWalkConfigurer = NullRevWalkConfigurer.INSTANCE;
		}
		this.revWalkConfigurer = revWalkConfigurer;
	}

	@Override
	public void setRevCommitFilter(RevCommitFilter revCommitFilter) {
		if (revCommitFilter == null) {
			revCommitFilter = NullRevCommitFilter.INSTANCE;
		}
		this.revCommitFilter = revCommitFilter;
	}

	protected RevWalk createRevWalk() throws IOException {
		Repository repository = gitRepository.getRepository();
		RevWalk revWalk = new RevWalk(repository);
		revWalkConfigurer.configure(revWalk);
		return revWalk;
	}

}