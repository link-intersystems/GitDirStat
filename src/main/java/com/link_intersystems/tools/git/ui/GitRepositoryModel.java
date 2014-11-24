package com.link_intersystems.tools.git.ui;

import java.io.File;

public class GitRepositoryModel extends AbstractPropertyChangeSupport {

	private File gitDir;
	private String repositoryId;

	public GitRepositoryModel() {
		gitDir = new File(System.getProperty("user.dir"));
	}

	public void setGitDir(File gitDir) {
		this.repositoryId = null;
		firePropertyChange("gitDir", this.gitDir, this.gitDir = gitDir);
	}

	public File getGitDir() {
		return gitDir;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		firePropertyChange("repositoryId", this.repositoryId,
				this.repositoryId = repositoryId);
	}

}
