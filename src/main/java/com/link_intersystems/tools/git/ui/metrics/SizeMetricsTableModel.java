package com.link_intersystems.tools.git.ui.metrics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.link_intersystems.tools.git.common.SortOrder;
import com.link_intersystems.tools.git.common.SortedMap;
import com.link_intersystems.tools.git.common.SortedMap.SortBy;
import com.link_intersystems.tools.git.domain.TreeObject;

public class SizeMetricsTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = -1102426074366547834L;

	private static final int COL_PATH_INDEX = 0;
	private static final int COL_SIZE_INDEX = 1;

	private List<String> pathList = new ArrayList<String>();
	private Map<String, TreeObject> pathMap = new HashMap<String, TreeObject>();
	private boolean sortAsc;

	public void setSortOrder(boolean sortAsc) {
		this.sortAsc = sortAsc;
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
			value = pathMap.get(path);
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

	public void setCommitRangeTree(TreeObject commitRangeTree) {
		if (commitRangeTree != null) {
			pathMap = commitRangeTree.asPathMap();

			pathMap = applySorting(pathMap);

			Set<String> parhKeySet = pathMap.keySet();
			pathList = new ArrayList<String>(parhKeySet);
		}
		fireTableDataChanged();
	}

	private Map<String, TreeObject> applySorting(Map<String, TreeObject> pathMap) {
		if (sortAsc) {
			pathMap = new SortedMap<String, TreeObject>(pathMap, SortBy.VALUE,
					SortOrder.ASC);
		} else {
			pathMap = new SortedMap<String, TreeObject>(pathMap, SortBy.VALUE,
					SortOrder.DESC);
		}
		return pathMap;
	}

}
