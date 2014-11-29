package com.link_intersystems.tools.git.ui.metrics;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class SizeMetricsTreeComponent extends JComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private SizeMetricsPropertyChangeListener sizeMetricsChangeListener = new SizeMetricsPropertyChangeListener();

	private SizeMetricsTreeModel sizeMetricsTreeModel = new SizeMetricsTreeModel();
	private JTree sizeMetricsTree = new JTree(sizeMetricsTreeModel);
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(sizeMetricsTree);

	private GitRepositoryModel gitRepositoryModel;

	public SizeMetricsTreeComponent() {
		setLayout(new BorderLayout());
		sizeMetricsTree
				.setCellRenderer(new HumanReadableFileSizeTreeCellRenderer());
		sizeMetricsTree.setRootVisible(false);
		add(sizeMetricsScrollPane, BorderLayout.CENTER);
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			gitRepositoryModel.removePropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					sizeMetricsChangeListener);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.addPropertyChangeListener(
					GitRepositoryModel.PROP_COMMIT_RANGE_TREE,
					sizeMetricsChangeListener);
			updateCommitRangeTree();
		}
	}

	public GitRepositoryModel getModel() {
		return gitRepositoryModel;
	}

	private void updateCommitRangeTree() {
		if (gitRepositoryModel != null) {
			TreeObject commitRangeTree = gitRepositoryModel
					.getCommitRangeTree();
			sizeMetricsTreeModel.setCommitRangeTree(commitRangeTree);
		}
	}

	private class SizeMetricsPropertyChangeListener implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateCommitRangeTree();
		}

	}
}
