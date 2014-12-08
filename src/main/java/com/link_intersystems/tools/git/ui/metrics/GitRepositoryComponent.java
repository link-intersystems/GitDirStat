package com.link_intersystems.tools.git.ui.metrics;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public abstract class GitRepositoryComponent extends JComponent {

	private static final long serialVersionUID = -3502081762632205262L;

	private class SizeMetricsPropertyChangeListener implements
			PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			updateCommitRangeTree();
		}

	}

	private GitRepositoryModel gitRepositoryModel;
	private SizeMetricsPropertyChangeListener sizeMetricsChangeListener = new SizeMetricsPropertyChangeListener();

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.removePropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					sizeMetricsChangeListener);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.addPropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					sizeMetricsChangeListener);
		}
		updateCommitRangeTree();
	}

	public GitRepositoryModel getModel() {
		return gitRepositoryModel;
	}

	protected  abstract void updateCommitRangeTree();
}
