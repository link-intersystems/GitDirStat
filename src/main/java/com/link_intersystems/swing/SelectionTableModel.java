package com.link_intersystems.swing;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class SelectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -1771060206519204339L;
	private static final TableModel EMPTY_TABLE_MODEL = new DefaultTableModel();
	private static final ListSelectionModel EMPTY_LIST_SELECTION_MODEL = new DefaultListSelectionModel();

	private TableModelSynchronization tableModelSynchronization = new TableModelSynchronization();
	private TableModel tableModel = EMPTY_TABLE_MODEL;
	private ListSelectionModel listSelectionModel = EMPTY_LIST_SELECTION_MODEL;

	public SelectionTableModel() {
	}

	public void setSelectionTable(TableModel tableModel,
			ListSelectionModel listSelectionModel) {
		if (this.tableModel != null) {
			this.tableModel.removeTableModelListener(tableModelSynchronization);
		}
		this.tableModel = tableModel;
		if (this.tableModel != null) {
			this.tableModel.addTableModelListener(tableModelSynchronization);
			this.listSelectionModel = listSelectionModel;
		} else {
			this.tableModel = EMPTY_TABLE_MODEL;
			this.listSelectionModel = EMPTY_LIST_SELECTION_MODEL;
		}
		fireTableStructureChanged();
	}

	@Override
	public int getRowCount() {
		return tableModel.getRowCount();
	}

	@Override
	public int getColumnCount() {
		int columnCount = tableModel.getColumnCount();
		return columnCount + 1;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Selected";
		} else {
			return tableModel.getColumnName(column - 1);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return Boolean.class;
		} else {
			return tableModel.getColumnClass(columnIndex - 1);
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			boolean selectedIndex = listSelectionModel
					.isSelectedIndex(rowIndex);
			return Boolean.valueOf(selectedIndex);
		} else {
			return tableModel.getValueAt(rowIndex, columnIndex - 1);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			Boolean selected = (Boolean) aValue;
			if (selected) {
				listSelectionModel.addSelectionInterval(rowIndex, rowIndex);
			} else {
				listSelectionModel.removeSelectionInterval(rowIndex, rowIndex);
			}
		}
	}

	private class TableModelSynchronization implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			fireTableStructureChanged();
		}

	}

}
