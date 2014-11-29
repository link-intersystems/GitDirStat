package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.swing.ComponentResize;
import com.link_intersystems.swing.RelativeWidthResizer;
import com.link_intersystems.swing.TableColumnResize;
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

		sizeMetricsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		TableColumnModel columnModel = sizeMetricsTable.getColumnModel();
		TableColumn firstColumn = columnModel.getColumn(0);
		TableColumn secondColumn = columnModel.getColumn(1);

		ComponentResize<TableColumn> columnResize = new TableColumnResize();
		RelativeWidthResizer<TableColumn> relativeWidthResizer = new RelativeWidthResizer<TableColumn>(
				columnResize);
		relativeWidthResizer.setRelativeWidth(firstColumn, 0.9);
		relativeWidthResizer.setRelativeWidth(secondColumn, 0.1);
		secondColumn.setMinWidth(50);

		relativeWidthResizer.apply(sizeMetricsTable);
		sizeMetricsTable.addComponentListener(relativeWidthResizer);
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
