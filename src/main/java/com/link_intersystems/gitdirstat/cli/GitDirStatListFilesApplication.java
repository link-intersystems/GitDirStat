package com.link_intersystems.gitdirstat.cli;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import com.link_intersystems.gitdirstat.GitDirStatArgumentsParseException;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.NullProgressListener;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.util.SortOrder;

public class GitDirStatListFilesApplication {

	public static void main(String[] args) throws Exception {
		GitDirStatListFilesArguments cliArguments;
		try {
			cliArguments = GitDirStatListFilesArguments.parse(args);
			GitDirStatListFilesApplication commandLineGitDirStatApplication = new GitDirStatListFilesApplication();
			commandLineGitDirStatApplication.run(cliArguments);
		} catch (GitDirStatArgumentsParseException e) {
			e.printHelp(System.err);
		}
	}

	public void run(GitDirStatListFilesArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitRepositoryDir);

		List<? extends Ref> refs = gitRepository.getRefs(Ref.class);
		TreeObject commitRangeTree = gitRepository.getCommitRangeTree(refs,
				NullProgressListener.INSTANCE);

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
