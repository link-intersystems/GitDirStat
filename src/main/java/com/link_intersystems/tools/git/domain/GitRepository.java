package com.link_intersystems.tools.git.domain;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatArguments;

public class GitRepository {

	private GitDirStatArguments arguments;
	private Repository repository;

	public GitRepository(GitDirStatArguments arguments) {
		this.arguments = arguments;
	}

	public Repository getRepository() throws IOException {
		if (this.repository == null) {

			File gitRepositoryDir = arguments.getGitRepositoryDir();
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.readEnvironment()
					.findGitDir(gitRepositoryDir).build();
			this.repository = repository;
		}
		return repository;
	}

	public CommitRange getCommitRange(String revstr) throws IOException {
		Repository repository = getRepository();
		AnyObjectId toInclusive = repository.resolve(revstr);
		AnyObjectId fromInclusive = getInitialCommit(toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	private RevCommit getInitialCommit(AnyObjectId headId) throws IOException {
		Repository repository = getRepository();
		RevWalk rw = new RevWalk(repository);
		RevCommit c = null;
		RevCommit root = rw.parseCommit(headId);
		rw.sort(RevSort.REVERSE);
		rw.markStart(root);
		c = rw.next();
		return c;
	}
}
