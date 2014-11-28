package com.link_intersystems.tools.git.ui;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.tools.git.common.SortedMap;
import com.link_intersystems.tools.git.common.SortedMap.SortBy;
import com.link_intersystems.tools.git.common.SortedMap.SortOrder;
import com.link_intersystems.tools.git.domain.TreeObject;

public class SizeMetricsTreeModel extends DefaultTreeModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private boolean sortAsc;

	public SizeMetricsTreeModel() {
		super(new WorkdirTreeNode());
	}

	public void setSortOrder(boolean sortAsc) {
		this.sortAsc = sortAsc;
	}

	public static class TreeObjectTreeNode extends DefaultMutableTreeNode {

		/**
		 *
		 */
		private static final long serialVersionUID = -4859433328865139220L;

		public TreeObjectTreeNode(String name) {
			super(null);
			TreeObjectModel treeObject = new TreeObjectModel(name, this);
			setUserObject(treeObject);
		}

		public void setSize(BigInteger size) {
			TreeObjectModel treeObject = getTreeObjectModel();
			treeObject.setSize(size);
		}

		TreeObjectModel getTreeObjectModel() {
			TreeObjectModel treeObject = (TreeObjectModel) getUserObject();
			return treeObject;
		}

		@Override
		public String toString() {
			return super.toString();
		}
	}

	public static class WorkdirTreeNode extends DefaultMutableTreeNode {
		/**
		 *
		 */
		private static final long serialVersionUID = -5276518794046807282L;

		private String[] splitPath(String path) {
			return StringUtils.split(path, '/');
		}

		@SuppressWarnings("unchecked")
		public TreeObjectTreeNode makePath(String path) {
			String[] splitPath = splitPath(path);

			DefaultMutableTreeNode parentTreeNode = this;
			TreeObjectTreeNode treeObjectTreeNode = null;
			TreeObjectTreeNode lastTreeObjectTreeNode = null;

			Enumeration<TreeObjectTreeNode> treeNodes = parentTreeNode
					.children();

			pathSegments: for (int i = 0; i < splitPath.length; i++) {
				String pathSegment = splitPath[i];

				while (treeNodes.hasMoreElements()) {
					treeObjectTreeNode = treeNodes.nextElement();
					TreeObjectModel treeObject = treeObjectTreeNode
							.getTreeObjectModel();
					if (pathSegment.equals(treeObject.getName())) {
						lastTreeObjectTreeNode = treeObjectTreeNode;
						parentTreeNode = treeObjectTreeNode;
						treeNodes = parentTreeNode.children();
						continue pathSegments;
					}
				}

				treeObjectTreeNode = new TreeObjectTreeNode(pathSegment);
				lastTreeObjectTreeNode = treeObjectTreeNode;
				parentTreeNode.add(treeObjectTreeNode);
				parentTreeNode = treeObjectTreeNode;
				treeNodes = parentTreeNode.children();
			}

			return lastTreeObjectTreeNode;

		}
	}

	public void setCommitRangeTree(TreeObject commitRangeTree) {
		setRoot(new WorkdirTreeNode());
		if (commitRangeTree == null) {
			return;
		}
		WorkdirTreeNode workdirTreeNode = (WorkdirTreeNode) getRoot();

		Map<String, TreeObject> pathMap = commitRangeTree.asPathMap();
		pathMap = applySorting(pathMap);
		for (Entry<String, TreeObject> pathEntry : pathMap.entrySet()) {
			String path = pathEntry.getKey();
			TreeObjectTreeNode treeObject = workdirTreeNode.makePath(path);
			BigInteger size = pathEntry.getValue().getSize();
			treeObject.setSize(size);

		}
		fireTreeStructureChanged(this, new Object[] { workdirTreeNode },
				new int[0], new Object[0]);
	}

	private Map<String, TreeObject> applySorting(Map<String, TreeObject> pathMap) {
		if (sortAsc) {
			pathMap = new SortedMap<String, TreeObject>(pathMap, SortBy.VALUE,
					SortOrder.ASC);
		} else {
			pathMap = new SortedMap<String, TreeObject>(pathMap, SortBy.VALUE,
					SortOrder.DESC);
		}
		return pathMap;
	}

}
