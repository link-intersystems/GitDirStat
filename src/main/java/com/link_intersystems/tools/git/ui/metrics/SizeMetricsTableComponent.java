package com.link_intersystems.tools.git.ui.metrics;

import java.awt.BorderLayout;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.swing.ComponentResize;
import com.link_intersystems.swing.RelativeWidthResizer;
import com.link_intersystems.swing.TableColumnResize;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class SizeMetricsTableComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private SizeMetricsTableModel sizeMetricsTableModel = new SizeMetricsTableModel();
	private JTable sizeMetricsTable = new JTable(sizeMetricsTableModel);
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(
			sizeMetricsTable);

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

		final ListSelectionModel selectionModel = sizeMetricsTable
				.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				int firstIndex = e.getFirstIndex();
				int lastIndex = e.getLastIndex();
				List<String> selectedPaths = new ArrayList<String>();
				for (int i = firstIndex; i <= lastIndex; i++) {
					if (selectionModel.isSelectedIndex(i)) {
						String path = sizeMetricsTableModel.getPath(i);
						selectedPaths.add(path);
					}
				}
				getModel().setSelectedPaths(selectedPaths);
			}
		});
	}

	protected void updateCommitRangeTree() {
		GitRepositoryModel gitRepositoryModel = getModel();
		if (gitRepositoryModel != null) {
			TreeObject commitRangeTree = gitRepositoryModel
					.getCommitRangeTree();
			sizeMetricsTableModel.setCommitRangeTree(commitRangeTree);
		}
	}

}
