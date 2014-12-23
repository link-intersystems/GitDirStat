package com.link_intersystems.gitdirstat.metrics;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.comparators.TransformingComparator;
import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.collections4.comparators.ReverseComparator;

import com.link_intersystems.gitdirstat.common.SortOrder;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeObjectPath;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;

public class GitRepositoryTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = -5276518794046807282L;

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

	@SuppressWarnings("unchecked")
	public DefaultMutableTreeNode addTreeObject(TreeObject newTreeObject) {
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