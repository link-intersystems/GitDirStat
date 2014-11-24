package com.link_intersystems.tools.git.cli;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
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

	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {

		Repository repository = getRepository(gitDirStatArguments);
		GitMetricsService gitMetricsService = new GitMetricsService(repository);
		CommitRange commitRange = getCommitRange(repository,
				gitDirStatArguments);
		SizeMetrics sizeMetrics = gitMetricsService.getSizeMetrics(commitRange);
		Map<String, BigInteger> pathSizes = sizeMetrics.getPathSizes();

		BigInteger max = Collections.max(pathSizes.values());
		int maxLength = max.toString().length();
		for (Entry<String, BigInteger> pathSize : pathSizes.entrySet()) {
			String sizeValue = pathSize.getValue().toString();
			String paddedSize = StringUtils.rightPad(sizeValue, maxLength);
			String path = pathSize.getKey();
			String pathSizeLine = format("{0} {1}", paddedSize, path);
			System.out.println(pathSizeLine);
		}

	}

	private Repository getRepository(GitDirStatArguments gitDirStatArguments)
			throws IOException {
		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.readEnvironment()
				.findGitDir(gitRepositoryDir).build();
		return repository;
	}

	private CommitRange getCommitRange(Repository repository,
			GitDirStatArguments gitDirStatArguments)
			throws AmbiguousObjectException, IncorrectObjectTypeException,
			IOException {
		AnyObjectId toInclusive = repository.resolve(Constants.HEAD);
		AnyObjectId fromInclusive = getInitialCommit(repository, toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	private RevCommit getInitialCommit(Repository repository, AnyObjectId headId) {
		RevWalk rw = new RevWalk(repository);
		RevCommit c = null;
		try {
			RevCommit root = rw.parseCommit(headId);
			rw.sort(RevSort.REVERSE);
			rw.markStart(root);
			c = rw.next();
			return c;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
