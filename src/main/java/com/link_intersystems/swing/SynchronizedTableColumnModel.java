package com.link_intersystems.swing;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.link_intersystems.beans.BeanPropertySync;

public class SynchronizedTableColumnModel extends DefaultTableColumnModel {

	private static final long serialVersionUID = -6008690732758899314L;

	private TableColumnModelSync tableColumnModelSync = new TableColumnModelSync();
	private TableColumnModel sourceModel;

	public void setSourceModel(TableColumnModel sourceModel) {
		if (this.sourceModel != null) {
			this.sourceModel.removeColumnModelListener(tableColumnModelSync);
			this.setSelectionModel(new DefaultListSelectionModel());
		}
		this.sourceModel = sourceModel;
		if (this.sourceModel != null) {
			this.sourceModel.addColumnModelListener(tableColumnModelSync);
			ListSelectionModel selectionModel = this.sourceModel
					.getSelectionModel();
			this.setSelectionModel(selectionModel);
			tableColumnModelSync.init(this.sourceModel);
		}
	}

	private class TableColumnModelSync implements TableColumnModelListener {

		private Map<TableColumn, TableColumnSync> propertySyncMap = new HashMap<TableColumn, TableColumnSync>();
		private Map<TableColumn, TableColumn> sourceToTargetMap = new HashMap<TableColumn, TableColumn>();

		@Override
		public void columnAdded(TableColumnModelEvent e) {
			TableColumnModel sourceModel = (TableColumnModel) e.getSource();
			for (int i = e.getFromIndex(); i < e.getToIndex(); i++) {
				TableColumn sourceColumn = sourceModel.getColumn(i);
				handleColumnAdded(sourceColumn);
			}
		}

		public void init(TableColumnModel sourceModel) {
			int columnCount = sourceModel.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				TableColumn sourceColumn = sourceModel.getColumn(i);
				handleColumnAdded(sourceColumn);
			}
		}

		private void handleColumnAdded(TableColumn sourceColumn) {
			TableColumn targetColumn = new TableColumn();
			copyProperties(sourceColumn, targetColumn);
			BeanPropertySync<TableColumn> beanPropertySync = new BeanPropertySync<TableColumn>(
					targetColumn);
			beanPropertySync.setSynchronization(sourceColumn);
			propertySyncMap.put(targetColumn, new TableColumnSync(sourceColumn,
					beanPropertySync));
			DefaultTableColumnModel targetModel = SynchronizedTableColumnModel.this;
			targetModel.addColumn(targetColumn);
			sourceToTargetMap.put(sourceColumn, targetColumn);
		}

		@Override
		public void columnRemoved(TableColumnModelEvent e) {
			TableColumnModel sourceModel = (TableColumnModel) e.getSource();
			for (int i = e.getFromIndex(); i < e.getToIndex(); i++) {
				TableColumn sourceColumn = sourceModel.getColumn(i);
				handleColumnRemoved(sourceColumn);
			}
		}

		private void handleColumnRemoved(TableColumn sourceColumn) {
			TableColumn targetColumn = sourceToTargetMap.get(sourceColumn);
			TableColumnSync tableColumnSync = propertySyncMap.get(targetColumn);
			DefaultTableColumnModel targetModel = SynchronizedTableColumnModel.this;
			targetModel.removeColumn(targetColumn);
			tableColumnSync.removePropertyChangeListener();
		}

		@Override
		public void columnMoved(TableColumnModelEvent e) {
		}

		private void copyProperties(TableColumn sourceColumn,
				TableColumn targetColumn) {
			int modelIndex = sourceColumn.getModelIndex();
			int width = sourceColumn.getWidth();
			TableCellRenderer cellRenderer = sourceColumn.getCellRenderer();
			TableCellEditor cellEditor = sourceColumn.getCellEditor();
			targetColumn.setModelIndex(modelIndex);
			targetColumn.setWidth(width);
			targetColumn.setCellRenderer(cellRenderer);
			targetColumn.setCellEditor(cellEditor);
		}

		@Override
		public void columnMarginChanged(ChangeEvent e) {
			TableColumnModel sourceModel = (TableColumnModel) e.getSource();
			DefaultTableColumnModel targetModel = SynchronizedTableColumnModel.this;
			targetModel.setColumnMargin(sourceModel.getColumnMargin());

		}

		@Override
		public void columnSelectionChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			DefaultTableColumnModel targetModel = SynchronizedTableColumnModel.this;
			targetModel.setSelectionModel((ListSelectionModel) e.getSource());
		}

	}

	private static class TableColumnSync {
		private TableColumn sourceColumn;
		private BeanPropertySync<TableColumn> beanPropertySync;

		public TableColumnSync(TableColumn sourceColumn,
				BeanPropertySync<TableColumn> beanPropertySync) {
			this.sourceColumn = sourceColumn;
			this.beanPropertySync = beanPropertySync;
		}

		public void removePropertyChangeListener() {
			this.sourceColumn.removePropertyChangeListener(beanPropertySync);
		}

	}
}
