package com.link_intersystems.tools.git.ui;

import java.math.BigInteger;
import java.util.Enumeration;

import com.link_intersystems.tools.git.ui.SizeMetricsTreeModel.TreeObjectTreeNode;

public class TreeObject {

	private String name;
	private BigInteger size = BigInteger.ZERO;
	private BigInteger totalSize;
	private TreeObjectTreeNode treeObjectTreeNode;

	public TreeObject(String name, TreeObjectTreeNode treeObjectTreeNode) {
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
		if (totalSize == null) {
			totalSize = getSize();
			Enumeration<TreeObjectTreeNode> breadthFirstEnumeration = treeObjectTreeNode
					.breadthFirstEnumeration();
			while (breadthFirstEnumeration.hasMoreElements()) {
				TreeObjectTreeNode treeNode = breadthFirstEnumeration
						.nextElement();
				TreeObject treeObject = (TreeObject) treeNode.getUserObject();
				if(treeObject == this){
					continue;
				}
				totalSize = totalSize.add(treeObject.getTotalSize());
			}
		}
		return totalSize;
	}

}
