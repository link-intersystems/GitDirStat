package com.link_intersystems.gitdirstat.ui.treeobjects;

import java.math.BigInteger;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.swing.AbstractListModelTableModelAdapter;
import com.link_intersystems.swing.SumTableModel;

public class TreeObjectTableModel extends
		AbstractListModelTableModelAdapter<TreeObject> {

	private static final long serialVersionUID = 1037872939612640074L;
	private static final int COL_PATH = 0;
	private static final int COL_SIZE = 1;

	private SumTableModel sumTableModel = new SumTableModel(this);

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public void setEntryModel(ListModel entryModel) {
		super.setEntryModel(entryModel);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> columnClass = null;
		switch (columnIndex) {
		case COL_PATH:
			columnClass = String.class;
			break;
		case COL_SIZE:
			columnClass = BigInteger.class;
			break;
		}
		return columnClass;
	}

	@Override
	public String getColumnName(int column) {
		String columnName = null;
		switch (column) {
		case COL_PATH:
			columnName = "Path";
			break;
		case COL_SIZE:
			columnName = "Total size";
			break;
		}
		return columnName;
	}

	@Override
	protected Object getColumnValue(TreeObject treeObject, int columnIndex) {
		Object columnValue = null;
		switch (columnIndex) {
		case COL_PATH:
			columnValue = treeObject.getRootRelativePath().getPathname();
			break;
		case COL_SIZE:
			columnValue = treeObject.getSize();
			break;
		}
		return columnValue;
	}

	public TableModel getSummaryTableModel() {
		return sumTableModel;
	}
}
