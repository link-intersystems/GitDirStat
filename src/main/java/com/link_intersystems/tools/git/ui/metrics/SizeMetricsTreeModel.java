package com.link_intersystems.tools.git.ui.metrics;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.comparators.ReverseComparator;

import com.link_intersystems.tools.git.common.SortOrder;
import com.link_intersystems.tools.git.common.SortedMap;
import com.link_intersystems.tools.git.common.SortedMap.SortBy;
import com.link_intersystems.tools.git.domain.TreeObject;
import com.link_intersystems.tools.git.domain.TreeObjectPath;
import com.link_intersystems.tools.git.domain.TreeObjectSortBy;

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

	public static class WorkdirTreeNode extends DefaultMutableTreeNode {
		/**
		 *
		 */
		private static final long serialVersionUID = -5276518794046807282L;

		public void deepSort(TreeObjectSortBy treeObjectSortOrder) {

			deepSort(this, treeObjectSortOrder, SortOrder.DESC);

		}

		@SuppressWarnings("unchecked")
		private void deepSort(DefaultMutableTreeNode parent,
				TreeObjectSortBy sortBy, SortOrder order) {
			Enumeration<DefaultMutableTreeNode> treeObjectNodes = parent
					.children();
			List<DefaultMutableTreeNode> treeNodes = EnumerationUtils
					.toList(treeObjectNodes);

			Comparator<DefaultMutableTreeNode> comparator = new BeanComparator<DefaultMutableTreeNode>(
					"userObject");
			if (SortOrder.DESC.equals(order)) {
				comparator = new ReverseComparator<DefaultMutableTreeNode>(
						comparator);
			}

			Collections.sort(treeNodes, comparator);

			parent.removeAllChildren();
			for (DefaultMutableTreeNode childNode : treeNodes) {
				parent.add(childNode);
				deepSort(childNode, sortBy, order);
			}

		}

		@SuppressWarnings("unchecked")
		public DefaultMutableTreeNode makePath(TreeObject newTreeObject) {
			DefaultMutableTreeNode parentTreeNode = this;
			DefaultMutableTreeNode newTreeNode = null;
			DefaultMutableTreeNode latestNode = null;

			Enumeration<DefaultMutableTreeNode> treeNodes = parentTreeNode
					.children();

			TreeObjectPath treeObjectPath = newTreeObject.getPath();

			Enumeration<TreeObject> treePathEnumeration = treeObjectPath
					.enumerate();
			treePathEnumeration.nextElement(); // pop root

			pathTreeObjects: while (treePathEnumeration.hasMoreElements()) {
				TreeObject pathTreeObject = treePathEnumeration.nextElement();

				while (treeNodes.hasMoreElements()) {
					newTreeNode = treeNodes.nextElement();
					TreeObject treeObject = (TreeObject) newTreeNode
							.getUserObject();
					if (pathTreeObject.getName().equals(treeObject.getName())) {
						latestNode = newTreeNode;
						parentTreeNode = newTreeNode;
						treeNodes = parentTreeNode.children();
						continue pathTreeObjects;
					}
				}

				newTreeNode = new DefaultMutableTreeNode(pathTreeObject);
				latestNode = newTreeNode;
				parentTreeNode.add(newTreeNode);
				parentTreeNode = newTreeNode;
				treeNodes = parentTreeNode.children();
			}

			return latestNode;

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
			workdirTreeNode.makePath(pathEntry.getValue());
		}
		workdirTreeNode.deepSort(TreeObjectSortBy.SIZE);
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
