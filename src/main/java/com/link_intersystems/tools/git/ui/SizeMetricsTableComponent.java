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

import com.link_intersystems.tools.git.service.SizeMetrics;

public class SizeMetricsTableComponent extends JComponent implements
		SizeMetricsView {

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
			sizeMetricsTableModel.setSizeMetrics(sizeMetrics);
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
