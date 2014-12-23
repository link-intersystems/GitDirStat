package com.link_intersystems.gitdirstat.metrics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import com.link_intersystems.gitdirstat.ui.GitRepositoryModel;

public abstract class GitRepositoryComponent extends JComponent {

	private static final long serialVersionUID = -3502081762632205262L;

	private class CommitRangeTreeeChangeListener implements
			PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			updateCommitRangeTree();
		}

	}

	private GitRepositoryModel gitRepositoryModel;
	private CommitRangeTreeeChangeListener commitRangeTreeChangeListener = new CommitRangeTreeeChangeListener();

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.removePropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					commitRangeTreeChangeListener);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.addPropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					commitRangeTreeChangeListener);
		}
		updateCommitRangeTree();
	}

	public GitRepositoryModel getModel() {
		return gitRepositoryModel;
	}

	protected  abstract void updateCommitRangeTree();
}
