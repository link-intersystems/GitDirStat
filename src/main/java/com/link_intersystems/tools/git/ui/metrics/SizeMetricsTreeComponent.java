package com.link_intersystems.tools.git.ui.metrics;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.link_intersystems.swing.PopupMenuMouseAdapter;
import com.link_intersystems.swing.RadioButtonGroupModel;
import com.link_intersystems.tools.git.common.SortOrder;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.domain.TreeObjectSortBy;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class SizeMetricsTreeComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private SizeMetricsTreeModel sizeMetricsTreeModel = new SizeMetricsTreeModel();
	private JTree sizeMetricsTree = new JTree(sizeMetricsTreeModel);
	private JScrollPane sizeMetricsScrollPane = new JScrollPane(sizeMetricsTree);

	private RadioButtonGroupModel sortByButtonGroupModel;
	private RadioButtonGroupModel sortOrderButtonGroupModel;

	public SizeMetricsTreeComponent() {
		setLayout(new BorderLayout());
		sizeMetricsTree
				.setCellRenderer(new HumanReadableFileSizeTreeCellRenderer());
		add(sizeMetricsScrollPane, BorderLayout.CENTER);

		createPopupMenu();
	}

	private void createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		JMenu sortMenu = new JMenu("Sorting");
		popup.add(sortMenu);
		sortByButtonGroupModel = createSortByMenuEntries(sortMenu);
		sortMenu.addSeparator();
		sortOrderButtonGroupModel = createSortOrderMenuEntries(sortMenu);

		SortingUpdateListener sortingUpdateListener = new SortingUpdateListener(
				sortByButtonGroupModel, sortOrderButtonGroupModel,
				sizeMetricsTreeModel);

		sortByButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);
		sortOrderButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);

		PopupMenuMouseAdapter popupMenuMouseAdapter = new PopupMenuMouseAdapter(
				popup);
		sizeMetricsTree.addMouseListener(popupMenuMouseAdapter);
	}

	private RadioButtonGroupModel createSortOrderMenuEntries(JMenu parent) {
		RadioButtonGroupModel sortOrderButtonGroupModel = new RadioButtonGroupModel();

		JRadioButtonMenuItem sortOrderAscSizeButton = new JRadioButtonMenuItem(
				"ascending");
		JRadioButtonMenuItem sortOrderDescSizeButton = new JRadioButtonMenuItem(
				"descending");

		sortOrderButtonGroupModel.add(sortOrderAscSizeButton, SortOrder.ASC);
		sortOrderButtonGroupModel.add(sortOrderDescSizeButton, SortOrder.DESC);
		sortOrderButtonGroupModel.setSelected(sortOrderDescSizeButton, true);

		parent.add(sortOrderAscSizeButton);
		parent.add(sortOrderDescSizeButton);

		return sortOrderButtonGroupModel;
	}

	private RadioButtonGroupModel createSortByMenuEntries(JMenu parent) {
		JRadioButtonMenuItem sortBySizeButton = new JRadioButtonMenuItem(
				"by size");
		JRadioButtonMenuItem sortByPathnameButton = new JRadioButtonMenuItem(
				"by pathname");

		RadioButtonGroupModel sortByButtonGroupModel = new RadioButtonGroupModel();
		sortByButtonGroupModel.add(sortBySizeButton, TreeObjectSortBy.SIZE);
		sortByButtonGroupModel.add(sortByPathnameButton, TreeObjectSortBy.NAME);
		sortByButtonGroupModel.setSelected(sortBySizeButton, true);

		parent.add(sortBySizeButton);
		parent.add(sortByPathnameButton);

		return sortByButtonGroupModel;
	}

	protected void updateCommitRangeTree() {
		GitRepositoryModel model = getModel();
		if (model != null) {
			TreeObject commitRangeTree = model.getCommitRangeTree();
			sizeMetricsTreeModel.setCommitRangeTree(commitRangeTree);
		}
	}

	private static class SortingUpdateListener implements
			PropertyChangeListener {

		private RadioButtonGroupModel sortByButtonGroupModel;
		private RadioButtonGroupModel sortOrderButtonGroupModel;
		private SizeMetricsTreeModel sizeMetricsTreeModel;

		public SortingUpdateListener(
				RadioButtonGroupModel sortByButtonGroupModel,
				RadioButtonGroupModel sortOrderButtonGroupModel,
				SizeMetricsTreeModel sizeMetricsTreeModel) {
			this.sortByButtonGroupModel = sortByButtonGroupModel;
			this.sortOrderButtonGroupModel = sortOrderButtonGroupModel;
			this.sizeMetricsTreeModel = sizeMetricsTreeModel;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			TreeObjectSortBy sortBy = (TreeObjectSortBy) sortByButtonGroupModel
					.getSelectionValue();
			SortOrder sortOrder = (SortOrder) sortOrderButtonGroupModel
					.getSelectionValue();
			sizeMetricsTreeModel.setSorting(sortBy, sortOrder);
		}
	}
}
