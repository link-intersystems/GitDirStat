package com.link_intersystems.tools.git.ui;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.link_intersystems.tools.git.service.SizeMetrics;

public class SizeMetricsTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1102426074366547834L;

	private static final int COL_PATH_INDEX = 0;
	private static final int COL_SIZE_INDEX = 1;

	private SizeMetrics sizeMetrics;
	private List<String> pathList = new ArrayList<String>();

	public void setSizeMetrics(SizeMetrics sizeMetrics) {
		this.sizeMetrics = sizeMetrics;
		Set<String> parhKeySet = sizeMetrics.getPathSizes().keySet();
		pathList = new ArrayList<String>(parhKeySet);
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return pathList.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;

		String path = pathList.get(rowIndex);
		switch (columnIndex) {
		case COL_PATH_INDEX:
			value = path;
			break;

		case COL_SIZE_INDEX:
			if (sizeMetrics != null) {
				Map<String, BigInteger> pathSizes = sizeMetrics.getPathSizes();
				value = pathSizes.get(path);
			}
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
