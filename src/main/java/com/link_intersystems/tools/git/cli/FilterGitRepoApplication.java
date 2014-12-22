package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.io.GlobPattern;
import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.IndexFilter;
import com.link_intersystems.tools.git.domain.GlobPathIndexFilter;

public class FilterGitRepoApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitRepositoryDir);

		CommitRange commitRange = gitRepository.getCommitRange(Constants.HEAD);

		Collection<CommitRange> commitRanges = Collections
				.singleton(commitRange);
		IndexFilter indexFilter = new GlobPathIndexFilter(new GlobPattern(
				"**/main/java/**/input/*.java"));
		gitRepository.applyFilter(commitRanges, indexFilter);

	}

}
