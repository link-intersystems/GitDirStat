package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.link_intersystems.tools.git.service.SizeMetrics;

public class SizeMetricsTreeComponent extends JComponent implements
		SizeMetricsView {

	private static final long serialVersionUID = 8588810751988085851L;

	private SizeMetricsPropertyChangeListener sizeMetricsChangeListener = new SizeMetricsPropertyChangeListener();

	private SizeMetricsTreeModel sizeMetricsTreeModel = new SizeMetricsTreeModel();
	private JTree sizeMetricsTree = new JTree(sizeMetricsTreeModel);
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(sizeMetricsTree);

	private GitRepositoryModel gitRepositoryModel;

	public SizeMetricsTreeComponent() {
		setLayout(new BorderLayout());
		sizeMetricsTree.setCellRenderer(new HumanReadableFileSizeTreeCellRenderer());
		sizeMetricsTree.setRootVisible(false);
		add(sizeMetricsScrollPane, BorderLayout.CENTER);
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			gitRepositoryModel.removePropertyChangeListener(
					GitRepositoryModel.PROP_SIZE_METRICS,
					sizeMetricsChangeListener);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel.addPropertyChangeListener(
					GitRepositoryModel.PROP_SIZE_METRICS,
					sizeMetricsChangeListener);
			updateSizeMetrics();
		}
	}

	public GitRepositoryModel getModel() {
		return gitRepositoryModel;
	}

	private void updateSizeMetrics() {
		if (gitRepositoryModel != null) {
			SizeMetrics sizeMetrics = gitRepositoryModel.getSizeMetrics();
			sizeMetricsTreeModel.setSizeMetrics(sizeMetrics);
		}
	}

	private class SizeMetricsPropertyChangeListener implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateSizeMetrics();
		}

	}
}
