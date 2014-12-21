package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.TreeObject;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitRepositoryDir);

		CommitRange commitRange = gitRepository.getCommitRange(Constants.HEAD);
		TreeObject commitRangeTree = gitRepository
				.getCommitRangeTree(commitRange);

		List<TreeObject> treeObjects = commitRangeTree.toFileList();

		PathListFormatter pathMapFormatter = new PathListFormatter(treeObjects);

		OutputStream outputStream = arguments.getOutputStream();
		pathMapFormatter.format(outputStream);
	}

}
