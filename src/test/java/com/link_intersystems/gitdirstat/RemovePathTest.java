package com.link_intersystems.gitdirstat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.PathDeletionIndexFilter;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.junit.jgit.ActualCommit;
import com.link_intersystems.junit.jgit.ActualTree;
import com.link_intersystems.junit.jgit.CommitMessageCommitSelection;
import com.link_intersystems.junit.jgit.CommitSelection;
import com.link_intersystems.junit.jgit.DefaultTestRepository;
import com.link_intersystems.junit.jgit.RepoAssertion;
import com.link_intersystems.junit.jgit.TestRepository;
import com.link_intersystems.junit.maven.TestEnvironmentProperties;

public class RemovePathTest {

	private TestRepository testRepository;

	@Before
	public void setup() throws IOException {
		TestEnvironmentProperties testEnvironmentProperties = new TestEnvironmentProperties();
		testRepository = new DefaultTestRepository(testEnvironmentProperties,
				"classpath:GitDirStat_simple_repository.zip");
	}

	@Test
	public void removePaths() throws Exception {
		GitRepository gitRepository = testRepository.getGitRepository();
		List<Ref> refs = gitRepository.getRefs(Ref.class);

		IndexFilter indexRewriter = new PathDeletionIndexFilter(Arrays.asList(
				"F", "E"));
		gitRepository.applyFilter(refs, indexRewriter);

		Repository repository = testRepository.getGit().getRepository();
		RepoAssertion repoAssertion = new RepoAssertion(repository);

		CommitSelection removedPathCommits = new CommitMessageCommitSelection(
				"E", "F");
		CommitTreeAssertion commitTreeAssertion = new CommitTreeAssertion() {

			private Collection<String> expectedRemovedPaths = new ArrayList<String>(
					Arrays.asList("E", "F"));

			@Override
			public void assertTree(ActualCommit actualCommit,
					ActualTree actualTree) {
				String path = actualTree.getPath();
				if (expectedRemovedPaths.contains(path)) {
					throw new AssertionError("Path " + path
							+ " should have been removed from commit "
							+ actualCommit);
				}
			}

		};
		repoAssertion
				.assertCommitTrees(removedPathCommits, commitTreeAssertion);

	}

	@After
	public void tearDown() throws IOException {
		testRepository.close();
	}

}
