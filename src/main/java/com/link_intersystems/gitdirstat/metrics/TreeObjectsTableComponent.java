package com.link_intersystems.gitdirstat.metrics;

import java.awt.BorderLayout;
import java.math.BigInteger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.gitdirstat.ui.GitRepositoryModel;
import com.link_intersystems.gitdirstat.ui.PathModel;
import com.link_intersystems.swing.AlternatingColorTableCellRenderer;
import com.link_intersystems.swing.ComponentResize;
import com.link_intersystems.swing.HumanReadableFileSizeTableCellRenderer;
import com.link_intersystems.swing.RelativeWidthResizer;
import com.link_intersystems.swing.RowSorterAwareListSelectionModelUpdater;
import com.link_intersystems.swing.TableColumnResize;

public class TreeObjectsTableComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private TreeObjectTableModel treeObjectsTableModel = new TreeObjectTableModel();

	private JTable treeObjectsTable = new JTable(treeObjectsTableModel);
	private JTable summaryTable = new JTable();
	private JScrollPane treeObjectsScrollPane = new JScrollPane(
			treeObjectsTable);

	private RowSorterAwareListSelectionModelUpdater rowSorterAwareListSelectionModelUpdater;

	public TreeObjectsTableComponent() {
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());

		add(northPanel, BorderLayout.NORTH);
		add(treeObjectsScrollPane, BorderLayout.CENTER);
		add(summaryTable, BorderLayout.SOUTH);

		treeObjectsTable.setRowSorter(new TableRowSorter<TableModel>(
				treeObjectsTableModel));

		TableCellRenderer defaultRenderer = new AlternatingColorTableCellRenderer();
		treeObjectsTable.setDefaultRenderer(String.class, defaultRenderer);

		AlternatingColorTableCellRenderer alternatingColorTableCellRenderer = new AlternatingColorTableCellRenderer(
				new HumanReadableFileSizeTableCellRenderer());

		treeObjectsTable.setDefaultRenderer(BigInteger.class,
				alternatingColorTableCellRenderer);

		treeObjectsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		summaryTable.setColumnModel(treeObjectsTable.getColumnModel());

		TableModel summaryTableModel = treeObjectsTableModel
				.getSummaryTableModel();
		summaryTable.setModel(summaryTableModel);
		summaryTable.setDefaultRenderer(BigInteger.class,
				new HumanReadableFileSizeTableCellRenderer());

		TableColumnModel columnModel = treeObjectsTable.getColumnModel();
		TableColumn firstColumn = columnModel.getColumn(0);
		firstColumn.setHeaderValue(treeObjectsTableModel.getColumnName(0));

		TableColumn secondColumn = columnModel.getColumn(1);
		secondColumn.setHeaderValue(treeObjectsTableModel.getColumnName(1));

		ComponentResize<TableColumn> columnResize = new TableColumnResize();
		RelativeWidthResizer<TableColumn> relativeWidthResizer = new RelativeWidthResizer<TableColumn>(
				columnResize);
		relativeWidthResizer.setRelativeWidth(firstColumn, 0.9);
		relativeWidthResizer.setRelativeWidth(secondColumn, 0.1);
		secondColumn.setMinWidth(50);

		relativeWidthResizer.apply(treeObjectsTable);
		treeObjectsTable.addComponentListener(relativeWidthResizer);
	}

	protected void updateCommitRangeTree() {
		GitRepositoryModel gitRepositoryModel = getModel();
		if (gitRepositoryModel != null) {
			PathModel pathModel = gitRepositoryModel.getPathListModel();
			ListModel entryModel = pathModel.getListModel();
			treeObjectsTableModel.setEntryModel(entryModel);
			if (rowSorterAwareListSelectionModelUpdater != null) {
				rowSorterAwareListSelectionModelUpdater.setSourceModel(null,
						null);
			}
			rowSorterAwareListSelectionModelUpdater = new RowSorterAwareListSelectionModelUpdater(
					pathModel.getListSelectionModel());
			rowSorterAwareListSelectionModelUpdater.setSourceModel(
					treeObjectsTable.getSelectionModel(),
					treeObjectsTable.getRowSorter());
		}
		ListSelectionModel selectionModel = treeObjectsTable
				.getSelectionModel();
		selectionModel.clearSelection();
	}

}
