package com.link_intersystems.gitdirstat.metrics;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigInteger;
import java.util.Enumeration;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;
import com.link_intersystems.gitdirstat.ui.GitRepositoryModel;
import com.link_intersystems.gitdirstat.ui.PathModel;
import com.link_intersystems.swing.FileModel;
import com.link_intersystems.swing.FileModelAdapterFactory;
import com.link_intersystems.swing.HumanReadableFileSizeTreeCellRenderer;
import com.link_intersystems.swing.RadioButtonGroupModel;
import com.link_intersystems.swing.ShowPopupMouseAdapter;
import com.link_intersystems.util.SortOrder;

public class TreeObjectsTreeComponent extends GitRepositoryComponent {

	private static final long serialVersionUID = 8588810751988085851L;

	private JTree treeObjectsTree = new JTree();
	private JScrollPane treeObjectsScrollPane = new JScrollPane(treeObjectsTree);

	private RadioButtonGroupModel sortByButtonGroupModel;
	private RadioButtonGroupModel sortOrderButtonGroupModel;

	private SortingUpdateListener sortingUpdateListener;

	public TreeObjectsTreeComponent() {
		setLayout(new BorderLayout());
		HumanReadableFileSizeTreeCellRenderer cellRenderer = new HumanReadableFileSizeTreeCellRenderer();
		TreeObjectFileModelAdapterFactory modelAdapterFactory = new TreeObjectFileModelAdapterFactory();
		cellRenderer.setFileModelAdapterFactory(modelAdapterFactory);
		treeObjectsTree.setCellRenderer(cellRenderer);
		treeObjectsTree.setRootVisible(false);
		treeObjectsTree.setExpandsSelectedPaths(true);
		add(treeObjectsScrollPane, BorderLayout.CENTER);

		createPopupMenu();
	}

	@Override
	public void beforeVisible() {
		collapseAll(treeObjectsTree);
	}

	private void collapseAll(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			TreeNode childAt = root.getChildAt(i);
			collapseAll(tree, new TreePath(new Object[] { root, childAt }));
		}
	}

	@SuppressWarnings("unchecked")
	private void collapseAll(JTree tree, TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
				TreeNode n = e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				collapseAll(tree, path);
			}
		}
		tree.collapsePath(parent);
		// tree.collapsePath(parent);
	}

	private void createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		JMenu sortMenu = new JMenu("Sorting");
		popup.add(sortMenu);
		sortByButtonGroupModel = createSortByMenuEntries(sortMenu);
		sortMenu.addSeparator();
		sortOrderButtonGroupModel = createSortOrderMenuEntries(sortMenu);

		sortingUpdateListener = new SortingUpdateListener(
				sortByButtonGroupModel, sortOrderButtonGroupModel);

		sortByButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);
		sortOrderButtonGroupModel.addPropertyChangeListener("selectedValue",
				sortingUpdateListener);

		ShowPopupMouseAdapter popupMenuMouseAdapter = new ShowPopupMouseAdapter(
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
			PathModel pathModel = model.getPathModel();
			TreeObjectsTreeModel treeModel = pathModel.getTreeModel();
			sortingUpdateListener.setTreeObjectsTreeModel(treeModel);
			treeObjectsTree.setModel(treeModel);
			TreeSelectionModel treeSelectionModel = pathModel
					.getTreeSelectionModel();
			treeObjectsTree.setSelectionModel(treeSelectionModel);
		}
	}

	private static class SortingUpdateListener implements
			PropertyChangeListener {

		private RadioButtonGroupModel sortByButtonGroupModel;
		private RadioButtonGroupModel sortOrderButtonGroupModel;
		private TreeObjectsTreeModel treeObjectsTreeModel;

		public SortingUpdateListener(
				RadioButtonGroupModel sortByButtonGroupModel,
				RadioButtonGroupModel sortOrderButtonGroupModel) {
			this.sortByButtonGroupModel = sortByButtonGroupModel;
			this.sortOrderButtonGroupModel = sortOrderButtonGroupModel;
		}

		public void setTreeObjectsTreeModel(
				TreeObjectsTreeModel treeObjectsTreeModel) {
			this.treeObjectsTreeModel = treeObjectsTreeModel;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (treeObjectsTreeModel != null) {
				TreeObjectSortBy sortBy = (TreeObjectSortBy) sortByButtonGroupModel
						.getSelectionValue();
				SortOrder sortOrder = (SortOrder) sortOrderButtonGroupModel
						.getSelectionValue();
				treeObjectsTreeModel.setSorting(sortBy, sortOrder);
			}
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
