package com.link_intersystems.gitdirstat.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.metrics.TreeObjectsTreeModel;
import com.link_intersystems.swing.AbstractPropertyChangeSupport;
import com.link_intersystems.swing.ListAdapterListModel;
import com.link_intersystems.swing.ListModelSelection;
import com.link_intersystems.swing.SelectionModel;

public class PathModel {

	public enum SelectionSource {
		LIST_SELECTION, TREE_SELECTION;
	}

	private ListAdapterListModel<TreeObject> listAdapterListModel = new ListAdapterListModel<TreeObject>();
	private ListSelectionModel pathSelectionModel = new DefaultListSelectionModel();
	private ListModelSelection<TreeObject> pathListModelSelection = new ListModelSelection<TreeObject>(
			listAdapterListModel, pathSelectionModel);
	private TreeObjectsTreeModel treeModel = new TreeObjectsTreeModel();
	private DefaultTreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
	private TreeModelSelection treeModelSelection = new TreeModelSelection(
			treeSelectionModel);

	private ProxySelectionModel<TreeObject> proxySelectionModel = new ProxySelectionModel<TreeObject>(
			pathListModelSelection);

	PathModel() {
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

	public void setSelectionSource(SelectionSource selectionSource) {
		List<TreeObject> selection = null;
		switch (selectionSource) {
		case LIST_SELECTION:
			selection = treeModelSelection.getSelection();
			pathSelectionModel.clearSelection();
			for (TreeObject treeObject : selection) {
				List<TreeObject> files = treeObject.toFileList();
				for (TreeObject file : files) {
					int indexOf = listAdapterListModel.indexOf(file);
					pathSelectionModel.addSelectionInterval(indexOf, indexOf);
				}
			}
			proxySelectionModel.setSelectionModel(pathListModelSelection);
			break;
		case TREE_SELECTION:
			selection = pathListModelSelection.getSelection();
			treeSelectionModel.clearSelection();
			for (TreeObject treeObject : selection) {
				TreePath treePath = treeModel.getTreePath(treeObject);
				treeSelectionModel.addSelectionPath(treePath);
			}
			proxySelectionModel.setSelectionModel(treeModelSelection);
			break;
		}
	}

	public ListModel getListModel() {
		return listAdapterListModel;
	}

	public ListSelectionModel getListSelectionModel() {
		return pathSelectionModel;
	}

	public SelectionModel<TreeObject> getSelectionModel() {
		return proxySelectionModel;
	}

	public TreeObjectsTreeModel getTreeModel() {
		return treeModel;
	}

	public void clear() {
		List<? extends TreeObject> emptyList = Collections.emptyList();
		listAdapterListModel.setList(emptyList);
		treeModel.setCommitRangeTree(null);
		pathSelectionModel.clearSelection();
		treeSelectionModel.clearSelection();
	}

	public TreeSelectionModel getTreeSelectionModel() {
		return treeSelectionModel;

	}

	private class TreeModelSelection extends AbstractPropertyChangeSupport
			implements SelectionModel<TreeObject> {

		private TreeSelectionModel treeSelectionModel;

		public TreeModelSelection(TreeSelectionModel treeSelectionModel) {
			this.treeSelectionModel = treeSelectionModel;
			treeSelectionModel
					.addTreeSelectionListener(new TreeSelectionListener() {

						@Override
						public void valueChanged(TreeSelectionEvent e) {
							TreeModelSelection.this.firePropertyChange(
									PROP_SELECTION, null, null);

						}
					});
		}

		@Override
		public boolean isEmpty() {
			return treeSelectionModel.isSelectionEmpty();
		}

		@Override
		public List<TreeObject> getSelection() {
			List<TreeObject> selection = new ArrayList<TreeObject>();
			TreePath[] selectionPaths = treeSelectionModel.getSelectionPaths();
			if (selectionPaths != null) {

				TreeObject treeObject = null;
				for (TreePath treePath : selectionPaths) {
					Object lastPathComponent = treePath.getLastPathComponent();

					lastPathComponent = unwrap(lastPathComponent);
					if (lastPathComponent instanceof TreeObject) {
						treeObject = (TreeObject) lastPathComponent;
						selection.add(treeObject);
					}
				}

			}
			return selection;
		}

		private Object unwrap(Object lastPathComponent) {
			if (lastPathComponent instanceof DefaultMutableTreeNode) {
				lastPathComponent = ((DefaultMutableTreeNode) lastPathComponent)
						.getUserObject();
			}
			return lastPathComponent;
		}

	}

	private class ProxySelectionModel<E> extends AbstractPropertyChangeSupport
			implements SelectionModel<E> {

		private class SelectionModelSync implements PropertyChangeListener {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String propertyName = evt.getPropertyName();
				if (PROP_SELECTION.equals(propertyName)) {
					ProxySelectionModel.this.firePropertyChange(PROP_SELECTION,
							null, getSelection());
				}
			}

		}

		private SelectionModel<E> selectionModel;
		private SelectionModelSync selectionModelSync = new SelectionModelSync();

		public ProxySelectionModel(SelectionModel<E> selectionModel) {
			this.selectionModel = selectionModel;
			setSelectionModel(selectionModel);
		}

		public void setSelectionModel(SelectionModel<E> selectionModel) {
			if (this.selectionModel != null) {
				this.selectionModel
						.removePropertyChangeListener(selectionModelSync);
			}
			this.selectionModel = selectionModel;
			if (this.selectionModel != null) {
				this.selectionModel
						.addPropertyChangeListener(selectionModelSync);
				firePropertyChange(PROP_SELECTION, null, getSelection());
			}
		}

		@Override
		public boolean isEmpty() {
			return selectionModel.isEmpty();
		}

		@Override
		public List<E> getSelection() {
			return selectionModel.getSelection();
		}
	}
}
