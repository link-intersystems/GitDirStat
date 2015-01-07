package com.link_intersystems.junit.jgit;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.junit.maven.TestEnvironmentProperties;

public class DefaultTestRepository implements TestRepository {

	private static final String CLASSPATH_PREFIX = "classpath:";
	private TestEnvironmentProperties testEnvironmentProperties;
	private String repositoryPath;
	private GitRepository gitRepository;
	private Git git;

	public DefaultTestRepository(
			TestEnvironmentProperties testEnvironmentProperties,
			String repositoryPath) {
		this.testEnvironmentProperties = testEnvironmentProperties;
		this.repositoryPath = repositoryPath;
	}

	public Git getGit() {
		if (git == null) {
			openRepository();
		}
		return git;
	}

	private void openRepository() {
		File outputDir = testEnvironmentProperties.getOutputDirectory();
		File gitRepositoryDir = new File(outputDir, "gitRepositories");

		try {
			FileSystemManager manager = VFS.getManager();
			if (repositoryPath.startsWith(CLASSPATH_PREFIX)) {
				String res = "res:"
						+ repositoryPath.substring(CLASSPATH_PREFIX.length());
				FileObject packFileObject = manager.resolveFile(res);
				try {

					FileObject zipFileSystem = manager
							.createFileSystem(packFileObject);

					try {

						FileObject targetDir = manager
								.toFileObject(gitRepositoryDir);
						targetDir.delete(Selectors.SELECT_ALL);
						targetDir.copyFrom(zipFileSystem, Selectors.SELECT_ALL);

						gitRepositoryDir = resolveRepositoryDirectory(gitRepositoryDir);
					} finally {
						zipFileSystem.close();
					}
				} finally {
					packFileObject.close();
				}

			} else {
				throw new UnsupportedOperationException(
						"GitRepository.value() must be a classpath:... resource");
			}
			RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
			repositoryBuilder.readEnvironment();
			repositoryBuilder.findGitDir(gitRepositoryDir);
			Repository repository = repositoryBuilder.build();
			git = new Git(repository);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private File resolveRepositoryDirectory(File unzipDir) {
		File[] listFiles = unzipDir.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].isDirectory()) {
				return listFiles[i];
			}
		}
		return unzipDir;
	}

	@Override
	public GitRepository getGitRepository() {
		if (gitRepository == null) {
			gitRepository = new GitRepository(getGit());
		}
		return gitRepository;
	}

	@Override
	public void close() {
		if (git != null) {
			git.close();
			gitRepository = null;
		}
	}

}
