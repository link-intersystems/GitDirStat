package com.link_intersystems.gitdirstat.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.metrics.TreeObjectsTreeModel;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;

public class PathModel {

	private ListAdapterListModel<TreeObject> listAdapterListModel = new ListAdapterListModel<TreeObject>();
	private ListSelectionModel pathSelectionModel = new DefaultListSelectionModel();
	private ListModelSelection<TreeObject> pathListModelSelection = new ListModelSelection<TreeObject>(
			listAdapterListModel, pathSelectionModel);
	private TreeObjectsTreeModel treeModel = new TreeObjectsTreeModel();
	private DefaultTreeSelectionModel defaultTreeSelectionModel = new DefaultTreeSelectionModel();
	private TreeSelectionModelSync treeSelectionModelSync = new TreeSelectionModelSync(
			defaultTreeSelectionModel);

	public PathModel() {
		pathSelectionModel.addListSelectionListener(treeSelectionModelSync);
	}

	public void setRootTreeObject(TreeObject treeObject) {
		if (treeObject == null) {
			clear();
			return;
		}
		List<TreeObject> fileList = treeObject.toFileList();
		Comparator<Object> reverseOrder = Collections.reverseOrder();
		Collections.sort(fileList, reverseOrder);
		listAdapterListModel.setList(fileList);
		treeModel.setCommitRangeTree(treeObject);
	}

	public ListModel getListModel() {
		return listAdapterListModel;
	}

	public ListSelectionModel getListSelectionModel() {
		return pathSelectionModel;
	}

	public ListModelSelection<TreeObject> getSelectionModel() {
		return pathListModelSelection;
	}

	public TreeObjectsTreeModel getTreeModel() {
		return treeModel;
	}

	public void clear() {
		List<? extends TreeObject> emptyList = Collections.emptyList();
		listAdapterListModel.setList(emptyList);
		treeModel.setCommitRangeTree(null);
		pathSelectionModel.clearSelection();
		defaultTreeSelectionModel.clearSelection();
	}

	public TreeSelectionModel getTreeSelectionModel() {
		return defaultTreeSelectionModel;

	}

	private class TreeSelectionModelSync implements ListSelectionListener {

		private DefaultTreeSelectionModel defaultTreeSelectionModel2;

		public TreeSelectionModelSync(
				DefaultTreeSelectionModel defaultTreeSelectionModel) {
			defaultTreeSelectionModel2 = defaultTreeSelectionModel;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			List<TreeObject> selection = pathListModelSelection.getSelection();
			defaultTreeSelectionModel2.clearSelection();
			for (TreeObject treeObject : selection) {
				TreePath treePath = treeModel.getTreePath(treeObject);
				defaultTreeSelectionModel2.addSelectionPath(treePath);
			}
		}

	}
}
