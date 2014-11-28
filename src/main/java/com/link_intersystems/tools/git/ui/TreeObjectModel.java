package com.link_intersystems.tools.git.ui;

import java.math.BigInteger;
import java.util.Enumeration;

import com.link_intersystems.tools.git.ui.SizeMetricsTreeModel.TreeObjectTreeNode;

public class TreeObjectModel {

	private String name;
	private BigInteger size = BigInteger.ZERO;
	private TreeObjectTreeNode treeObjectTreeNode;

	public TreeObjectModel(String name, TreeObjectTreeNode treeObjectTreeNode) {
		this.name = name;
		this.treeObjectTreeNode = treeObjectTreeNode;
	}

	public void setSize(BigInteger size) {
		this.size = size;
	}

	public BigInteger getSize() {
		return size;
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public BigInteger getTotalSize() {
		BigInteger totalSize = getSize();
		Enumeration<TreeObjectTreeNode> nodeEnumeration = treeObjectTreeNode
				.children();
		while (nodeEnumeration.hasMoreElements()) {
			TreeObjectTreeNode treeNode = nodeEnumeration.nextElement();
			TreeObjectModel treeObject = (TreeObjectModel) treeNode.getUserObject();
			totalSize = totalSize.add(treeObject.getTotalSize());
		}
		return totalSize;
	}
}
