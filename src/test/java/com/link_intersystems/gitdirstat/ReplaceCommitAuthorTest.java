package com.link_intersystems.gitdirstat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.link_intersystems.gitdirstat.domain.CommitUpdate;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.junit.jgit.CommitAssertion;
import com.link_intersystems.junit.jgit.CommitAuthorAssertion;
import com.link_intersystems.junit.jgit.CommitSelection;
import com.link_intersystems.junit.jgit.DefaultTestRepository;
import com.link_intersystems.junit.jgit.RepoAssertion;
import com.link_intersystems.junit.jgit.RevstrCommitSelection;
import com.link_intersystems.junit.jgit.TestRepository;
import com.link_intersystems.junit.maven.TestEnvironmentProperties;

public class ReplaceCommitAuthorTest {

	private static final String OLD_AUTHOR_EMAIL = "rene.link@link-intersystems.com";
	private static final String OLD_AUTHOR_NAME = "René Link";
	private static final String NEW_AUTHOR_EMAIL = "nomail";
	private static final String NEW_AUTHOR_NAME = "René";
	private TestRepository testRepository;

	@Before
	public void setup() throws IOException {
		TestEnvironmentProperties testEnvironmentProperties = new TestEnvironmentProperties();
		testRepository = new DefaultTestRepository(testEnvironmentProperties,
				"classpath:GitDirStat_simple_repository.zip");
	}

	@Test
	public void replaceCommitAuthor() throws Exception {
		GitRepository gitRepository = testRepository.getGitRepository();
		List<Ref> refs = gitRepository.getRefs(Ref.class);

		final List<String> commitsToRewrite = Arrays.asList("2f48838",
				"479cf94", "93d8dbd", "0908cde");

		IndexFilter indexRewriter = new IndexFilter() {

			@Override
			public void apply(CommitUpdate commitUpdate) throws IOException {
				String abbreviatedId = commitUpdate.getAbbreviatedId();
				if (commitsToRewrite.contains(abbreviatedId)) {
					commitUpdate.setAuthor(NEW_AUTHOR_NAME, NEW_AUTHOR_EMAIL);
				}
			}
		};
		gitRepository.applyFilter(refs, indexRewriter);

		Repository repository = testRepository.getGit().getRepository();
		RepoAssertion repoAssertion = new RepoAssertion(repository);

		CommitAssertion rewrittenAuthorAssertion = new CommitAuthorAssertion(
				NEW_AUTHOR_NAME, NEW_AUTHOR_EMAIL);
		CommitSelection rewrittenCommits = new RevstrCommitSelection(
				commitsToRewrite);
		repoAssertion.assertThatAllCommits(rewrittenCommits,
				rewrittenAuthorAssertion);

		CommitAssertion oldAuthorAssertion = new CommitAuthorAssertion(
				OLD_AUTHOR_NAME, OLD_AUTHOR_EMAIL);
		CommitSelection oldCommits = new RevstrCommitSelection("d01c671",
				"cf8ed37", "000287e", "866211b", "4f62e7e", "b402de7");
		repoAssertion.assertThatAllCommits(oldCommits, oldAuthorAssertion);

	}

	@After
	public void tearDown() throws IOException {
		testRepository.close();
	}

}
