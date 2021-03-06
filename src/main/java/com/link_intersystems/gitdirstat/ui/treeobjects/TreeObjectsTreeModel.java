package com.link_intersystems.gitdirstat.ui.treeobjects;

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.EnumerationIterator;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;
import com.link_intersystems.util.SortOrder;

public class TreeObjectsTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	private TreeObjectSortBy treeObjectSortBy = TreeObjectSortBy.SIZE;
	private SortOrder sortOrder = SortOrder.DESC;

	public TreeObjectsTreeModel() {
		super(new GitRepositoryTreeNode());
	}

	public void setSorting(TreeObjectSortBy treeObjectSortBy,
			SortOrder sortOrder) {
		this.treeObjectSortBy = treeObjectSortBy;
		this.sortOrder = sortOrder;
		GitRepositoryTreeNode workdirTreeNode = (GitRepositoryTreeNode) getRoot();
		workdirTreeNode.deepSort(treeObjectSortBy, sortOrder);
		fireTreeStructureChanged();
	}

	private void fireTreeStructureChanged() {
		GitRepositoryTreeNode workdirTreeNode = (GitRepositoryTreeNode) getRoot();
		fireTreeStructureChanged(this, new Object[] { workdirTreeNode },
				new int[0], new Object[0]);
	}

	public void setCommitRangeTree(TreeObject commitRangeTree) {
		GitRepositoryTreeNode gitRepositoryTreeNode = (GitRepositoryTreeNode) getRoot();
		gitRepositoryTreeNode.removeAllChildren();

		if (commitRangeTree == null) {
			return;
		}

		gitRepositoryTreeNode.setUserObject(commitRangeTree);
		createTreeObjectNodes(gitRepositoryTreeNode, commitRangeTree);
		gitRepositoryTreeNode.deepSort(treeObjectSortBy, sortOrder);

		fireTreeStructureChanged();
	}

	private void createTreeObjectNodes(
			GitRepositoryTreeNode gitRepositoryTreeNode,
			TreeObject commitRangeTree) {

		Iterator<Object> leafNodeIterator = createLeafNodeIterator(commitRangeTree);

		while (leafNodeIterator.hasNext()) {
			TreeObject treeObject = (TreeObject) leafNodeIterator.next();
			gitRepositoryTreeNode.addTreeObject(treeObject);
		}
	}

	private Iterator<Object> createLeafNodeIterator(TreeObject commitRangeTree) {
		Iterator<Object> objectGraphIterator = IteratorUtils
				.objectGraphIterator(commitRangeTree,
						new TreeObjectLeafNodeTransformer());
		return objectGraphIterator;
	}

	private class TreeObjectLeafNodeTransformer implements
			Transformer<Object, Object> {

		@Override
		public Object transform(Object input) {
			fireTreeStructureChanged();
			if (input instanceof TreeObject) {
				TreeObject treeObject = (TreeObject) input;
				if (treeObject.isFile()) {
					return treeObject;
				} else {
					return new EnumerationIterator<TreeObject>(
							treeObject.children());
				}
			}
			Class<?> clazz = null;
			if (input != null) {
				clazz = input.getClass();
			}
			throw new ClassCastException("Can only handle TreeObjects but was "
					+ clazz);
		}

	}

	public TreePath getTreePath(TreeObject treeObject) {
		GitRepositoryTreeNode gitRepositoryTreeNode = (GitRepositoryTreeNode) getRoot();
		DefaultMutableTreeNode treeNode = gitRepositoryTreeNode
				.findTreeNode(treeObject);

		TreePath treePath = null;
		if (treeNode != null) {
			treePath = new TreePath(treeNode.getPath());
		}
		return treePath;
	}
}
