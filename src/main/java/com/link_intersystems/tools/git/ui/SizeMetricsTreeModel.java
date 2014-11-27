package com.link_intersystems.tools.git.ui;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.tools.git.service.SizeMetrics;

public class SizeMetricsTreeModel extends DefaultTreeModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SizeMetricsTreeModel() {
		super(new WorkdirTreeNode());
	}

	public void setSizeMetrics(SizeMetrics sizeMetrics) {
		setRoot(new WorkdirTreeNode());
		if (sizeMetrics == null) {
			return;
		}
		WorkdirTreeNode workdirTreeNode = (WorkdirTreeNode) getRoot();

		for (Entry<String, BigInteger> entry : sizeMetrics.getPathSizes()
				.entrySet()) {
			String path = entry.getKey();
			TreeObjectTreeNode treeObject = workdirTreeNode.makePath(path);
			BigInteger size = entry.getValue();
			treeObject.setSize(size);

		}
		fireTreeStructureChanged(this, new Object[] { workdirTreeNode },
				new int[0], new Object[0]);

	}

	public static class TreeObjectTreeNode extends DefaultMutableTreeNode {

		/**
		 *
		 */
		private static final long serialVersionUID = -4859433328865139220L;

		public TreeObjectTreeNode(String name) {
			super(null);
			TreeObject treeObject = new TreeObject(name, this);
			setUserObject(treeObject);
		}

		public void setSize(BigInteger size) {
			TreeObject treeObject = getTreeObject();
			treeObject.setSize(size);
		}

		TreeObject getTreeObject() {
			TreeObject treeObject = (TreeObject) getUserObject();
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
					TreeObject treeObject = treeObjectTreeNode.getTreeObject();
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

}
