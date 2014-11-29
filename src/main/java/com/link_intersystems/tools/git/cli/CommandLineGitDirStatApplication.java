package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.common.SortedMap;
import com.link_intersystems.tools.git.common.SortedMap.SortBy;
import com.link_intersystems.tools.git.common.SortedMap.SortOrder;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.domain.TreeObjectSortOrder;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitRepositoryDir);

		CommitRange commitRange = gitRepository.getCommitRange(Constants.HEAD);
		TreeObject commitRangeTree = gitRepository
				.getCommitRangeTree(commitRange);

		Map<String, TreeObject> pathMap = commitRangeTree.asPathMap();

		SortOrder sortOrder = arguments.getSortOrder();
		SortBy sortBy = getPathMapSortOrder(arguments);

		SortedMap<String, TreeObject> sortedPathMap = new SortedMap<String, TreeObject>(
				pathMap, sortBy, sortOrder);
		PathMapFormatter pathMapFormatter = new PathMapFormatter(sortedPathMap);

		OutputStream outputStream = arguments.getOutputStream();
		pathMapFormatter.format(outputStream);
	}

	private SortBy getPathMapSortOrder(GitDirStatArguments arguments) {
		TreeObjectSortOrder sortBy = arguments.getSortBy();
		SortBy pathMapSortBy = null;
		switch (sortBy) {
		case SIZE:
			pathMapSortBy = SortBy.KEY;
			break;
		case NAME:
			pathMapSortBy = SortBy.KEY;
			break;
		default:
			break;
		}
		return pathMapSortBy;
	}
}
