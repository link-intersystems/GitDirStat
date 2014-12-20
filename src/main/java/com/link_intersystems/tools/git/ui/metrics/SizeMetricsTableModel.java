package com.link_intersystems.tools.git.ui.metrics;

import java.math.BigInteger;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import com.link_intersystems.tools.git.domain.TreeObject;

public class SizeMetricsTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1102426074366547834L;

	private static final int COL_PATH_INDEX = 0;
	private static final int COL_SIZE_INDEX = 1;

	private ListModel listModel;

	public SizeMetricsTableModel(ListModel listModel) {
		this.listModel = listModel;
		this.listModel.addListDataListener(new ListDataListener() {

			@Override
			public void intervalRemoved(ListDataEvent e) {
				fireTableDataChanged();
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				fireTableDataChanged();
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				fireTableDataChanged();
			}
		});
	}

	private TreeObject getTreeObject(int row) {
		return (TreeObject) listModel.getElementAt(row);
	}

	public String getPath(int row) {
		TreeObject treeObject = getTreeObject(row);
		return treeObject.getPath().getPathname();
	}

	@Override
	public int getRowCount() {
		return listModel.getSize();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		TreeObject treeObject = getTreeObject(rowIndex);
		switch (columnIndex) {
		case COL_PATH_INDEX:
			value = treeObject.getRootRelativePath().getPathname();
			break;

		case COL_SIZE_INDEX:
			value = treeObject.getSize();
			break;
		}
		return value;
	}

	@Override
	public String getColumnName(int column) {
		String columnName = null;
		switch (column) {
		case COL_PATH_INDEX:
			columnName = "Path";
			break;
		case COL_SIZE_INDEX:
			columnName = "Total size";
			break;
		}
		return columnName;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> columnClass = null;
		switch (columnIndex) {
		case COL_PATH_INDEX:
			columnClass = String.class;
			break;
		case COL_SIZE_INDEX:
			columnClass = BigInteger.class;
			break;
		}
		return columnClass;
	}

}
