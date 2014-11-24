package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.service.GitMetricsService;
import com.link_intersystems.tools.git.service.SizeMetrics;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		Repository repository = getRepository(arguments);
		GitMetricsService gitMetricsService = new GitMetricsService(repository);
		CommitRange commitRange = getCommitRange(repository);
		SizeMetrics sizeMetrics = gitMetricsService.getSizeMetrics(commitRange);

		SizesMetricsFormatter sizesMetricsFormatter = new SizesMetricsFormatter(
				sizeMetrics);

		OutputStream outputStream = arguments.getOutputStream();
		sizesMetricsFormatter.format(outputStream);
	}

	private Repository getRepository(GitDirStatArguments arguments)
			throws IOException {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.readEnvironment()
				.findGitDir(gitRepositoryDir).build();
		return repository;
	}

	private CommitRange getCommitRange(Repository repository)
			throws IOException {
		AnyObjectId toInclusive = repository.resolve(Constants.HEAD);
		AnyObjectId fromInclusive = getInitialCommit(repository, toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	private RevCommit getInitialCommit(Repository repository, AnyObjectId headId)
			throws IOException {
		RevWalk rw = new RevWalk(repository);
		RevCommit c = null;
		RevCommit root = rw.parseCommit(headId);
		rw.sort(RevSort.REVERSE);
		rw.markStart(root);
		c = rw.next();
		return c;
	}
}
