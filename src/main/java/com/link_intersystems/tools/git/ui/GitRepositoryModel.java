package com.link_intersystems.tools.git.ui;

import java.io.File;

import com.link_intersystems.tools.git.service.SizeMetrics;

public class GitRepositoryModel extends AbstractPropertyChangeSupport {

	public static final String PROP_REPOSITORY_ID = "repositoryId";
	public static final String PROP_GIT_DIR = "gitDir";
	public static final String PROP_SIZE_METRICS = "sizeMetrics";

	private File gitDir;
	private String repositoryId;

	private SizeMetrics sizeMetrics;

	public GitRepositoryModel() {
		gitDir = new File(System.getProperty("user.dir"));
	}

	public void setGitDir(File gitDir) {
		this.repositoryId = null;
		firePropertyChange(PROP_GIT_DIR, this.gitDir, this.gitDir = gitDir);
	}

	public File getGitDir() {
		return gitDir;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		firePropertyChange(PROP_REPOSITORY_ID, this.repositoryId,
				this.repositoryId = repositoryId);
	}

	public SizeMetrics getSizeMetrics() {
		return sizeMetrics;
	}

	public void setSizeMetrics(SizeMetrics sizeMetrics) {
		firePropertyChange(PROP_SIZE_METRICS, this.sizeMetrics,
				this.sizeMetrics = sizeMetrics);
	}

}
