package com.link_intersystems.tools.git.ui.metrics;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.link_intersystems.swing.FileModel;
import com.link_intersystems.swing.FileModelAdapterFactory;
import com.link_intersystems.swing.HumanReadableFileSizeTreeCellRenderer;
import com.link_intersystems.swing.PopupMenuMouseAdapter;
import com.link_intersystems.swing.RadioButtonGroupModel;
import com.link_intersystems.tools.git.common.SortOrder;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.domain.TreeObjectSortBy;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class TreeObjectsTreeComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private TreeObjectsTreeModel treeObjectsTreeModel = new TreeObjectsTreeModel();
	private JTree treeObjectsTree = new JTree(treeObjectsTreeModel);
	private JScrollPane treeObjectsScrollPane = new JScrollPane(treeObjectsTree);

	private RadioButtonGroupModel sortByButtonGroupModel;
	private RadioButtonGroupModel sortOrderButtonGroupModel;

	public TreeObjectsTreeComponent() {
		setLayout(new BorderLayout());
		HumanReadableFileSizeTreeCellRenderer cellRenderer = new HumanReadableFileSizeTreeCellRenderer();
		TreeObjectFileModelAdapterFactory modelAdapterFactory = new TreeObjectFileModelAdapterFactory();
		cellRenderer.setFileModelAdapterFactory(modelAdapterFactory);
		treeObjectsTree.setCellRenderer(cellRenderer);
		add(treeObjectsScrollPane, BorderLayout.CENTER);

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
				treeObjectsTreeModel);

		sortByButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);
		sortOrderButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);

		PopupMenuMouseAdapter popupMenuMouseAdapter = new PopupMenuMouseAdapter(
				popup);
		treeObjectsTree.addMouseListener(popupMenuMouseAdapter);
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
			treeObjectsTreeModel.setCommitRangeTree(commitRangeTree);
		}
	}

	private static class SortingUpdateListener implements
			PropertyChangeListener {

		private RadioButtonGroupModel sortByButtonGroupModel;
		private RadioButtonGroupModel sortOrderButtonGroupModel;
		private TreeObjectsTreeModel treeObjectsTreeModel;

		public SortingUpdateListener(
				RadioButtonGroupModel sortByButtonGroupModel,
				RadioButtonGroupModel sortOrderButtonGroupModel,
				TreeObjectsTreeModel treeObjectsTreeModel) {
			this.sortByButtonGroupModel = sortByButtonGroupModel;
			this.sortOrderButtonGroupModel = sortOrderButtonGroupModel;
			this.treeObjectsTreeModel = treeObjectsTreeModel;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			TreeObjectSortBy sortBy = (TreeObjectSortBy) sortByButtonGroupModel
					.getSelectionValue();
			SortOrder sortOrder = (SortOrder) sortOrderButtonGroupModel
					.getSelectionValue();
			treeObjectsTreeModel.setSorting(sortBy, sortOrder);
		}
	}

	private static class TreeObjectFileModel implements FileModel {

		private TreeObject treeObject;

		public TreeObjectFileModel(TreeObject treeObject) {
			this.treeObject = treeObject;
		}

		@Override
		public String getName() {
			return treeObject.getName();
		}

		@Override
		public BigInteger getSize() {
			return treeObject.getSize();
		}

	}

	private static class TreeObjectFileModelAdapterFactory implements
			FileModelAdapterFactory {

		@Override
		public FileModel createAdapter(Object treeModelObject) {
			FileModel fileModel = null;
			TreeObject treeObject = null;
			if (treeModelObject instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode mutableTreeNode = DefaultMutableTreeNode.class
						.cast(treeModelObject);
				Object userObject = mutableTreeNode.getUserObject();
				if (userObject instanceof TreeObject) {
					treeObject = TreeObject.class.cast(userObject);
				}
			}

			if (treeObject != null) {
				fileModel = new TreeObjectFileModel(treeObject);
			}

			return fileModel;
		}

	}
}
