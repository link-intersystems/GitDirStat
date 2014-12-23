package com.link_intersystems.swing;

import java.math.BigInteger;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.link_intersystems.math.Aggregate;
import com.link_intersystems.math.BigIntegerSum;

public class SumTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3604774516854551825L;
	/**
	 * {@value}
	 */
	private static final String SUM_SYMBOL = "\u03A3";
	private static final String DEFAULT_SUMMARY_COLUMN_NAME_FORMAT = "%s %s";

	private TableModelSync tableModelSync = new TableModelSync();
	private TableModel tableModel;
	private Aggregate<? extends Number>[] aggregateColumns;
	private String columnNameFormat = DEFAULT_SUMMARY_COLUMN_NAME_FORMAT;

	public SumTableModel(TableModel tableModel) {
		setTableModel(tableModel);
	}

	public void setTableModel(TableModel tableModel) {
		if (this.tableModel != null) {
			this.tableModel.removeTableModelListener(tableModelSync);
		}
		this.tableModel = tableModel;
		if (this.tableModel != null) {
			this.tableModel.addTableModelListener(tableModelSync);
			tableModelUpdated();
		}
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public String getColumnName(int column) {
		String columnName = tableModel.getColumnName(column);
		String summaryColumnName = String.format(columnNameFormat, SUM_SYMBOL,
				columnName);
		return summaryColumnName;
	}

	@Override
	public int getColumnCount() {
		return tableModel.getColumnCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Aggregate<? extends Number> columnAggregate = getColumnAggregate(columnIndex);
		return columnAggregate.getValue();
	}

	private Aggregate<? extends Number> getColumnAggregate(int columnIndex) {
		Aggregate<? extends Number> aggregate = this.aggregateColumns[columnIndex];
		if (aggregate == null) {
			Class<?> columnClass = tableModel.getColumnClass(columnIndex);
			Aggregate<? extends Number> columnAggregate = createAggregate(columnClass);
			createSummary(columnAggregate, columnIndex);
			aggregateColumns[columnIndex] = columnAggregate;
			aggregate = columnAggregate;
		}
		return aggregate;
	}

	private void createSummary(Aggregate<? extends Number> columnAggregate,
			int columnIndex) {
		int rowCount = tableModel.getRowCount();
		if (BigInteger.class.equals(tableModel.getColumnClass(columnIndex))) {
			for (int i = 0; i < rowCount; i++) {
				BigInteger columnValue = (BigInteger) tableModel.getValueAt(i,
						columnIndex);
				columnAggregate.addValue(columnValue);
			}
		} else {
			columnAggregate.addValue(rowCount);
		}

	}

	private Aggregate<? extends Number> createAggregate(Class<?> columnClass) {
		return new BigIntegerSum();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return tableModel.getColumnClass(columnIndex);
	}

	@SuppressWarnings("unchecked")
	private void tableModelUpdated() {
		this.aggregateColumns = new Aggregate[getColumnCount()];
	}

	private class TableModelSync implements TableModelListener {

		@Override
		public void tableChanged(TableModelEvent e) {
			tableModelUpdated();
		}

	}
}