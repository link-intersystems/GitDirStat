package com.link_intersystems.gitdirstat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.link_intersystems.gitdirstat.domain.CommitUpdate;
import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.gitdirstat.domain.TreeFileUpdate;
import com.link_intersystems.gitdirstat.domain.TreeUpdate;
import com.link_intersystems.gitdirstat.test.GitRepositoryLocation;
import com.link_intersystems.gitdirstat.test.GitRepositoryTestRunner;

@RunWith(GitRepositoryTestRunner.class)
@GitRepositoryLocation(value = "classpath:GitDirStat_simple_repository.zip")
public class GitRepositoryTest {

	@Test
	public void applyIndexFilterOnAuthor_(GitRepository gitRepository)
			throws IOException, GitAPIException {

		List<Ref> refs = gitRepository.getRefs(Ref.class);

		IndexFilter indexRewriter = new IndexFilter() {

			private List<String> filterMessages = Arrays.asList("D\n", "E\n",
					"F\n", "H\n");

			@Override
			public void apply(CommitUpdate commitUpdate) throws IOException {
				String originalMessage = commitUpdate.getOriginalMessage();
				if (filterMessages.contains(originalMessage)) {
					commitUpdate.setAuthor("René Link", "nomail");
				}

				TreeUpdate treeUpdate = commitUpdate.getTreeUpdate();
				while (treeUpdate.hasNext()) {
					TreeFileUpdate treeFile = treeUpdate.next();
					String path = treeFile.getPath();
					if (path.equals("A") || path.equals("D")) {
						treeFile.delete();
					}

				}
			}
		};
		gitRepository.applyFilter(refs, indexRewriter);
	}
}
