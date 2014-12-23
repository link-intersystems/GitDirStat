package com.link_intersystems.gitdirstat.cli;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.util.SortOrder;

public class CommandLineGitDirStatApplication {

	public static void main(String[] args) throws Exception {
		CommandLineGitDirStatArguments cliArguments = CommandLineGitDirStatArguments
				.parse(args);
		CommandLineGitDirStatApplication commandLineGitDirStatApplication = new CommandLineGitDirStatApplication();
		commandLineGitDirStatApplication.run(cliArguments);
	}

	public void run(CommandLineGitDirStatArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitRepositoryDir);

		CommitRange commitRange = gitRepository.getCommitRange(Constants.HEAD);
		TreeObject commitRangeTree = gitRepository
				.getCommitRangeTree(commitRange);

		List<TreeObject> treeObjects = commitRangeTree.toFileList();

		SortOrder sortOrder = arguments.getSortOrder();
		if (SortOrder.DESC.equals(sortOrder)) {
			Collections.sort(treeObjects, Collections.reverseOrder());
		} else {
			Collections.sort(treeObjects);
		}

		PathListFormatter pathMapFormatter = new PathListFormatter(treeObjects);

		OutputStream outputStream = arguments.getOutputStream();
		PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(
				outputStream, Charset.defaultCharset()));
		pathMapFormatter.format(printWriter);
		printWriter.flush();
		printWriter.close();
	}

}
