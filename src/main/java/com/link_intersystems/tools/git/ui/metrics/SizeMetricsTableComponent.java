package com.link_intersystems.tools.git.ui.metrics;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.swing.ComponentResize;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;
import com.link_intersystems.swing.RelativeWidthResizer;
import com.link_intersystems.swing.TableColumnResize;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class SizeMetricsTableComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private ListAdapterListModel<TreeObject> listAdapterListModel = new ListAdapterListModel<TreeObject>();
	private SizeMetricsTableModel sizeMetricsTableModel = new SizeMetricsTableModel(listAdapterListModel);
	private JTable sizeMetricsTable = new JTable(sizeMetricsTableModel);
	private JTable summaryTable = new JTable();
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(
			sizeMetricsTable);

	private DefaultTableModel dataModel;

	public SizeMetricsTableComponent() {
		setLayout(new BorderLayout());
		add(sizeMetricsScrollPane, BorderLayout.CENTER);
		add(summaryTable, BorderLayout.SOUTH);


		sizeMetricsTable.setRowSorter(new TableRowSorter<TableModel>(
				sizeMetricsTableModel));
		sizeMetricsTable.setDefaultRenderer(BigInteger.class,
				new HumanReadableFileSizeTableCellRenderer());

		sizeMetricsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		summaryTable.setColumnModel(sizeMetricsTable.getColumnModel());
		dataModel = new DefaultTableModel(0, 2);
		summaryTable.setModel(dataModel);

		TableColumnModel columnModel = sizeMetricsTable.getColumnModel();
		TableColumn firstColumn = columnModel.getColumn(0);
		firstColumn.setHeaderValue(sizeMetricsTableModel.getColumnName(0));

		TableColumn secondColumn = columnModel.getColumn(1);
		secondColumn.setHeaderValue(sizeMetricsTableModel.getColumnName(1));

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
		ListModelSelection<TreeObject> listModelSelection = new ListModelSelection<TreeObject>(listAdapterListModel, selectionModel);
		listModelSelection.setRowSorter(sizeMetricsTable.getRowSorter());
		listModelSelection.addPropertyChangeListener(ListModelSelection.PROP_SELECTION, new PropertyChangeListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ListModelSelection<TreeObject> source = (ListModelSelection<TreeObject>) evt.getSource();
				List<TreeObject> selection = source.getSelection();
				List<String> selectedPaths = new ArrayList<String>();
				for (TreeObject treeObject : selection) {
					selectedPaths.add(treeObject.getRootRelativePath().getPathname());
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

			List<TreeObject> fileList = commitRangeTree.toFileList();
			Comparator<TreeObject> reverseOrder = Collections.reverseOrder();
			Collections.sort(fileList, reverseOrder);
			listAdapterListModel.setList(fileList);
			Object pathCount = listAdapterListModel.getSize();
			Object repoSize = 0;

			if(dataModel.getRowCount() > 0){
				dataModel.removeRow(0);
			}
			dataModel.addRow(new Object[]{pathCount, repoSize});
		}
		ListSelectionModel selectionModel = sizeMetricsTable
				.getSelectionModel();
		selectionModel.clearSelection();
	}

}
