package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.link_intersystems.swing.ComponentResize;
import com.link_intersystems.swing.RelativeWidthResizer;
import com.link_intersystems.swing.SelectionTableModel;
import com.link_intersystems.swing.TableColumnResize;
import com.link_intersystems.tools.git.domain.Ref;

public class BranchSelectionTable extends JPanel {

	private static final long serialVersionUID = 8285821725678169740L;

	private GitRepositorySynchronization gitRepositorySynchronization = new GitRepositorySynchronization();
	private GitRepositoryModel gitRepositoryModel;

	private BranchTableModel tableModel = new BranchTableModel();
	private SelectionTableModel selectionTableModel = new SelectionTableModel();
	private DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
	private JTable selectionTable = new JTable();

	private RelativeWidthResizer<TableColumn> relativeWidthResizer;

	public BranchSelectionTable() {
		setLayout(new BorderLayout());

		selectionTableModel.setSelectionTable(tableModel, selectionModel);
		selectionTable.setSelectionModel(selectionModel);
		selectionTable.setModel(selectionTableModel);

		selectionTable.setSelectionBackground(selectionTable.getBackground());
		selectionTable.setSelectionForeground(selectionTable.getForeground());

		selectionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		add(new JScrollPane(selectionTable), BorderLayout.CENTER);
	}

	public Action createApplyBranchSelectionAction() {
		ApplyBranchSelectionAction applyBranchSelectionAction = new ApplyBranchSelectionAction();
		return applyBranchSelectionAction;
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.removePropertyChangeListener(gitRepositorySynchronization);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.addPropertyChangeListener(gitRepositorySynchronization);
		}
		setTableModel(gitRepositoryModel);
	}

	private void setTableModel(GitRepositoryModel gitRepositoryModel) {
		tableModel.setModel(gitRepositoryModel);

		TableColumnModel columnModel = selectionTable.getColumnModel();

		if (columnModel.getColumnCount() > 1) {

			TableColumn firstColumn = columnModel.getColumn(0);
			TableColumn secondColumn = columnModel.getColumn(1);

			ComponentResize<TableColumn> columnResize = new TableColumnResize();
			selectionTable.removeComponentListener(relativeWidthResizer);
			relativeWidthResizer = new RelativeWidthResizer<TableColumn>(
					columnResize);
			relativeWidthResizer.setRelativeWidth(firstColumn, 0.1);
			relativeWidthResizer.setRelativeWidth(secondColumn, 0.9);
			firstColumn.setMinWidth(50);

			relativeWidthResizer.apply(selectionTable);
			selectionTable.addComponentListener(relativeWidthResizer);
		}
	}

	public List<Ref> getSelectedRefs() {
		List<Ref> refs = new ArrayList<Ref>();

		ListSelectionModel selectionModel = selectionTable.getSelectionModel();

		int firstIndex = selectionModel.getMinSelectionIndex();
		int lastIndex = selectionModel.getMaxSelectionIndex();
		List<? extends Ref> repoRefs = gitRepositoryModel.getRefs();
		for (int i = firstIndex; i <= lastIndex; i++) {
			if (selectionModel.isSelectedIndex(i)) {
				Ref ref = repoRefs.get(i);
				refs.add(ref);
			}
		}

		return refs;
	}

	private class ApplyBranchSelectionAction extends AbstractAction {

		private static final long serialVersionUID = 6047021841050442726L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Ref> selectedRefs = getSelectedRefs();
			gitRepositoryModel.setSelectedRefs(selectedRefs);
		}

	}

	private static class GitRepositorySynchronization implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (GitRepositoryModel.PROP_REFS.equals(propertyName)) {
			}
		}

	}

	private static class BranchTableModel extends AbstractTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 996846783184052277L;

		private class GitRepositorySynchronization implements
				PropertyChangeListener {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (GitRepositoryModel.PROP_REFS.equals(propertyName)) {
					fireTableDataChanged();
				}
			}

		}

		private GitRepositorySynchronization gitRepositorySynchronization = new GitRepositorySynchronization();
		private GitRepositoryModel gitRepositoryModel;

		@Override
		public int getRowCount() {
			List<? extends Ref> refs = gitRepositoryModel.getRefs();
			return refs.size();
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return String.class;
			default:
				return super.getColumnClass(columnIndex);
			}
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "Ref";
			default:
				return super.getColumnName(column);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
			case 0:
				List<? extends Ref> refs = gitRepositoryModel.getRefs();
				Ref ref = refs.get(rowIndex);
				return ref.getName();
			default:
				return null;
			}
		}

		public void setModel(GitRepositoryModel gitRepositoryModel) {
			if (this.gitRepositoryModel != null) {
				this.gitRepositoryModel
						.removePropertyChangeListener(gitRepositorySynchronization);
			}
			this.gitRepositoryModel = gitRepositoryModel;
			if (this.gitRepositoryModel != null) {
				this.gitRepositoryModel
						.addPropertyChangeListener(gitRepositorySynchronization);
			}
		}
	}

}
