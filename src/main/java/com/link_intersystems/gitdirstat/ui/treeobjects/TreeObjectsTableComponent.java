package com.link_intersystems.gitdirstat.ui.treeobjects;

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
import com.link_intersystems.swing.SynchronizedTableColumnModel;
import com.link_intersystems.swing.TableColumnResize;

public class TreeObjectsTableComponent extends GitRepositoryComponent {

	private static final int COLUMN_SIZE = 1;

	private static final int COLUMN_PATH = 0;

	private static final long serialVersionUID = 8588810751988085851L;

	private JPanel northPanel = new JPanel(new BorderLayout());

	private JTable summaryTable = new JTable();
	private TreeObjectTableModel treeObjectsModel = new TreeObjectTableModel();
	private TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>();
	private TableCellRenderer defaultRenderer = new AlternatingColorTableCellRenderer();
	private TableCellRenderer humanReadableFileSizeCellRenderer = new HumanReadableFileSizeTableCellRenderer();
	private TableCellRenderer alternatingColorHumanReadableCellRenderer = new AlternatingColorTableCellRenderer(
			humanReadableFileSizeCellRenderer);
	private ComponentResize<TableColumn> tableColumnResize = new TableColumnResize();
	private RelativeWidthResizer<TableColumn> relativeColumnWidthResize = new RelativeWidthResizer<TableColumn>(
			tableColumnResize);
	private RowSorterAwareListSelectionModelUpdater rowSorterAwareListSelectionModelUpdater = new RowSorterAwareListSelectionModelUpdater();
	private JTable treeObjectsTable = new JTable(treeObjectsModel);

	private JScrollPane treeObjectsScrollPane = new JScrollPane(
			treeObjectsTable);

	public TreeObjectsTableComponent() {
		configureThis();
		configureTreeObjectsTable();
		configureSummaryTable();
	}

	private void configureThis() {
		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(treeObjectsScrollPane, BorderLayout.CENTER);
	}

	private void configureTreeObjectsTable() {
		rowSorter.setModel(treeObjectsModel);
		treeObjectsTable.setRowSorter(rowSorter);

		treeObjectsTable.setDefaultRenderer(String.class, defaultRenderer);
		treeObjectsTable.setDefaultRenderer(BigInteger.class,
				alternatingColorHumanReadableCellRenderer);

		treeObjectsTable.addComponentListener(relativeColumnWidthResize);

		rowSorterAwareListSelectionModelUpdater.setSourceModel(
				treeObjectsTable.getSelectionModel(),
				treeObjectsTable.getRowSorter());

		configureTreeObjectsTableColumns();
	}

	private void configureTreeObjectsTableColumns() {
		TableColumnModel columnModel = treeObjectsTable.getColumnModel();

		TableColumn pathColumn = columnModel.getColumn(COLUMN_PATH);
		pathColumn.setHeaderValue(treeObjectsModel.getColumnName(COLUMN_PATH));
		relativeColumnWidthResize.setRelativeWidth(pathColumn, 0.9);

		TableColumn sizeColumn = columnModel.getColumn(1);
		sizeColumn.setHeaderValue(treeObjectsModel.getColumnName(COLUMN_SIZE));
		sizeColumn.setMinWidth(50);
		relativeColumnWidthResize.setRelativeWidth(sizeColumn, 0.1);
	}

	private void configureSummaryTable() {
		TableModel summaryTableModel = treeObjectsModel.getSummaryTableModel();
		summaryTable.setModel(summaryTableModel);
		summaryTable.setDefaultRenderer(BigInteger.class,
				new HumanReadableFileSizeTableCellRenderer());
		SynchronizedTableColumnModel synchronizedTableColumnModel = new SynchronizedTableColumnModel();
		synchronizedTableColumnModel.setSourceModel(treeObjectsTable
				.getColumnModel());
		summaryTable.setColumnModel(synchronizedTableColumnModel);
		add(summaryTable, BorderLayout.SOUTH);
	}

	protected void updateCommitRangeTree() {
		GitRepositoryModel gitRepositoryModel = getModel();
		if (gitRepositoryModel != null) {
			PathModel pathModel = gitRepositoryModel.getPathModel();
			ListModel entryModel = pathModel.getListModel();
			treeObjectsModel.setEntryModel(entryModel);
			rowSorterAwareListSelectionModelUpdater.setTargetModel(pathModel
					.getListSelectionModel());
		}
		ListSelectionModel selectionModel = treeObjectsTable
				.getSelectionModel();
		selectionModel.clearSelection();
	}

}
