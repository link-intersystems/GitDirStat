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

public class GitRepository {

	private Repository repository;

	public GitRepository(Repository repository) {
		this.repository = repository;
	}

	static String createId(File repositoryDirectory) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.readEnvironment();
		builder.findGitDir(repositoryDirectory);
		File gitDir = builder.getGitDir();
		try {
			return gitDir.getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getId() {
		try {
			return getRepository().getDirectory().getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public Repository getRepository() {
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
