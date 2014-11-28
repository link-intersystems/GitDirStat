package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.tools.git.domain.TreeObject;

public class SizeMetricsTableComponent extends JComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private SizeMetricsPropertyChangeListener sizeMetricsChangeListener = new SizeMetricsPropertyChangeListener();
	private SizeMetricsTableModel sizeMetricsTableModel = new SizeMetricsTableModel();
	private JTable sizeMetricsTable = new JTable(sizeMetricsTableModel);
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(
			sizeMetricsTable);

	private GitRepositoryModel gitRepositoryModel;

	public SizeMetricsTableComponent() {
		setLayout(new BorderLayout());
		add(sizeMetricsScrollPane, BorderLayout.CENTER);
		sizeMetricsTable.setRowSorter(new TableRowSorter<TableModel>(
				sizeMetricsTableModel));
		sizeMetricsTable.setDefaultRenderer(BigInteger.class,
				new HumanReadableFileSizeTableCellRenderer());
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
			sizeMetricsTableModel.setCommitRangeTree(commitRangeTree);
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
