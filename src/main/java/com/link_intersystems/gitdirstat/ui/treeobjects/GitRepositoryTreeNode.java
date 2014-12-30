package com.link_intersystems.gitdirstat.ui.treeobjects;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.comparators.TransformingComparator;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.comparators.ReverseComparator;

import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectPath;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;
import com.link_intersystems.util.SortOrder;

public class GitRepositoryTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -5276518794046807282L;

	private Map<TreeObject, DefaultMutableTreeNode> treeObject2Nodes = new HashMap<TreeObject, DefaultMutableTreeNode>();

	public void deepSort(TreeObjectSortBy treeObjectSortOrder) {
		deepSort(this, treeObjectSortOrder, SortOrder.DESC);
	}

	public void deepSort(TreeObjectSortBy treeObjectSortOrder,
			SortOrder sortOrder) {
		deepSort(this, treeObjectSortOrder, sortOrder);
	}

	@SuppressWarnings("unchecked")
	private void deepSort(DefaultMutableTreeNode parent,
			TreeObjectSortBy sortBy, SortOrder order) {
		Enumeration<DefaultMutableTreeNode> treeObjectNodes = parent.children();
		List<DefaultMutableTreeNode> treeNodes = EnumerationUtils
				.toList(treeObjectNodes);

		BeanToPropertyValueTransformer sortProperty = getSortByTransformer(sortBy);

		Comparator<DefaultMutableTreeNode> comparator = new TransformingComparator(
				sortProperty);

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

	@Override
	public void setUserObject(Object userObject) {
		treeObject2Nodes.clear();
		super.setUserObject(userObject);
		treeObject2Nodes.put((TreeObject) userObject, this);
	}

	private BeanToPropertyValueTransformer getSortByTransformer(
			TreeObjectSortBy sortBy) {
		String sortProperty = null;
		switch (sortBy) {
		case NAME:
			sortProperty = "userObject.path.pathname";
			break;
		case SIZE:
			sortProperty = "userObject.size";
			break;

		default:
			sortProperty = "userObject";
			break;
		}
		BeanToPropertyValueTransformer beanValueTransformer = new BeanToPropertyValueTransformer(
				sortProperty);
		return beanValueTransformer;
	}

	public DefaultMutableTreeNode addTreeObject(TreeObject treeObject) {
		TreeObjectPath treeObjectPath = treeObject.getPath();
		Enumeration<TreeObject> treePathEnumeration = treeObjectPath
				.enumerate();

		DefaultMutableTreeNode previousNode = null;
		while (treePathEnumeration.hasMoreElements()) {
			TreeObject pathSegment = treePathEnumeration.nextElement();

			DefaultMutableTreeNode treeNode = treeObject2Nodes.get(pathSegment);
			if (treeNode == null) {
				treeNode = new DefaultMutableTreeNode(pathSegment);
				if (previousNode != null) {
					previousNode.add(treeNode);
				}
				treeObject2Nodes.put(pathSegment, treeNode);
			}
			previousNode = treeNode;
		}

		return previousNode;

	}

	public DefaultMutableTreeNode findTreeNode(TreeObject treeObject) {
		return treeObject2Nodes.get(treeObject);
	}

}