package com.link_intersystems.gitdirstat.cli;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.gitdirstat.GitDirStatApplication;
import com.link_intersystems.gitdirstat.GitDirStatArguments;
import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.GlobPathIndexFilter;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.io.GlobPattern;

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
