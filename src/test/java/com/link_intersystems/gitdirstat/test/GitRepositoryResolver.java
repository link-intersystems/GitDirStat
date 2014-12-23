package com.link_intersystems.gitdirstat.test;

import java.io.File;
import java.lang.annotation.Annotation;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

public class GitRepositoryResolver {

	private static final String CLASSPATH_PREFIX = "classpath:";
	private GitRepositoryTestRunner gitRepositoryTestRunner;
	private TestEnvironmentProperties testEnvironmentProperties;

	public GitRepositoryResolver(
			GitRepositoryTestRunner gitRepositoryTestRunner,
			TestEnvironmentProperties testEnvironmentProperties) {
		this.gitRepositoryTestRunner = gitRepositoryTestRunner;
		this.testEnvironmentProperties = testEnvironmentProperties;
	}

	public Git getRepository(FrameworkMethod frameworkMethod) {
		Git git = null;
		GitRepositoryLocation gitRepositoryAnnotation = getGitRepositoryAnnotation(frameworkMethod);
		if (gitRepositoryAnnotation != null) {
			String gitRepoValue = gitRepositoryAnnotation.value();
			File outputDir = testEnvironmentProperties.getOutputDirectory();
			File gitRepositoryDir = new File(outputDir, "gitRepositories");

			try {
				FileSystemManager manager = VFS.getManager();
				if (gitRepoValue.startsWith(CLASSPATH_PREFIX)) {
					String res = "res:"
							+ gitRepoValue.substring(CLASSPATH_PREFIX.length());
					FileObject packFileObject = manager.resolveFile(res);
					try {

						FileObject zipFileSystem = manager
								.createFileSystem(packFileObject);

						try {

							FileObject targetDir = manager
									.toFileObject(gitRepositoryDir);
							targetDir.delete(Selectors.SELECT_ALL);
							targetDir.copyFrom(zipFileSystem,
									Selectors.SELECT_ALL);

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
		return git;
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

	private GitRepositoryLocation getGitRepositoryAnnotation(
			FrameworkMethod frameworkMethod) {
		GitRepositoryLocation gitRepository = frameworkMethod
				.getAnnotation(GitRepositoryLocation.class);

		if (gitRepository == null) {
			TestClass testClass = gitRepositoryTestRunner.getTestClass();
			Annotation[] annotations = testClass.getAnnotations();
			for (int i = 0; i < annotations.length; i++) {
				if (GitRepositoryLocation.class.isInstance(annotations[i])) {
					gitRepository = GitRepositoryLocation.class.cast(annotations[i]);
				}
			}
		}

		return gitRepository;
	}
}
