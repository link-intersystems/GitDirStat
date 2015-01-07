package com.link_intersystems.junit.jgit;

import org.eclipse.jgit.api.Git;

import com.link_intersystems.gitdirstat.domain.GitRepository;

public interface TestRepository {

	GitRepository getGitRepository();

	Git getGit();

	void close();

}
