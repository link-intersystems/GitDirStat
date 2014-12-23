package com.link_intersystems.gitdirstat.domain;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.collections4.iterators.IteratorEnumeration;
import org.apache.commons.lang3.StringUtils;

public class TreeObject implements Comparable<TreeObject> {

	private List<TreeObject> children = new ArrayList<TreeObject>();
	private List<ObjectSize> objectSizes = new ArrayList<ObjectSize>();
	private String name;
	private BigInteger size;
	private TreeObject parent;
	private TreeObjectPath rootRelativePath;
	private TreeObjectPath thisPath;

	TreeObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	void addObjectSize(ObjectSize objectSize) {
		objectSizes.add(objectSize);
		this.size = null;
	}

	public BigInteger getSize() {
		if (size == null) {
			size = BigInteger.ZERO;
			for (ObjectSize objectSize : objectSizes) {
				BigInteger thisSize = objectSize.getSize();
				size = size.add(thisSize);
			}

			Enumeration<TreeObject> childEnumerations = children();
			while (childEnumerations.hasMoreElements()) {
				TreeObject childTreeObject = childEnumerations.nextElement();
				BigInteger childSize = childTreeObject.getSize();
				size = size.add(childSize);
			}
		}
		return size;
	}

	public List<TreeObject> toFileList() {
		List<TreeObject> fileList = new ArrayList<TreeObject>();

		if (this.isFile()) {
			fileList.add(this);
		}

		Enumeration<TreeObject> children = children();
		while (children.hasMoreElements()) {
			TreeObject treeObject = children.nextElement();
			List<TreeObject> childFiles = treeObject.toFileList();
			fileList.addAll(childFiles);
		}

		return fileList;
	}

	public Enumeration<TreeObject> children() {
		return new IteratorEnumeration<TreeObject>(children.iterator());
	}

	void add(TreeObject childTreeObject) {
		this.children.add(childTreeObject);
		childTreeObject.parent = this;
	}

	public TreeObject makePath(String path) {
		String[] splitPath = splitPath(path);

		TreeObject parentTreeObj = this;
		TreeObject currTreeObj = null;
		TreeObject lastTreeObj = null;

		Enumeration<TreeObject> treeNodes = parentTreeObj.children();

		pathSegments: for (int i = 0; i < splitPath.length; i++) {
			String pathSegment = splitPath[i];

			while (treeNodes.hasMoreElements()) {
				currTreeObj = treeNodes.nextElement();
				if (pathSegment.equals(currTreeObj.getName())) {
					lastTreeObj = currTreeObj;
					parentTreeObj = currTreeObj;
					treeNodes = parentTreeObj.children();
					continue pathSegments;
				}
			}

			currTreeObj = new TreeObject(pathSegment);
			lastTreeObj = currTreeObj;
			parentTreeObj.add(currTreeObj);
			parentTreeObj = currTreeObj;
			treeNodes = parentTreeObj.children();
		}

		return lastTreeObj;

	}

	private String[] splitPath(String path) {
		return StringUtils.split(path, '/');
	}

	public boolean isFile() {
		return this.children.isEmpty();
	}

	public TreeObjectPath getPath() {
		if (thisPath == null) {
			thisPath = new TreeObjectPath(this);
			if (parent != null) {
				TreeObjectPath parentPath = parent.getPath();
				thisPath.prepend(parentPath);
			}
		}
		return thisPath;
	}

	public TreeObjectPath getRootRelativePath() {
		if (rootRelativePath == null) {
			rootRelativePath = new TreeObjectPath(this);
			if (parent != null && parent.parent != null) {
				TreeObjectPath parentPath = parent.getRootRelativePath();
				rootRelativePath.prepend(parentPath);
			}
		}
		return rootRelativePath;
	}

	public Map<String, TreeObject> asRootRelativePathMap() {
		Stack<TreeObject> treeObjectStack = new Stack<TreeObject>();
		treeObjectStack.push(this);

		Map<String, TreeObject> pathMap = new HashMap<String, TreeObject>();

		TreeObject currTreeObject = null;
		while (!treeObjectStack.isEmpty()) {
			currTreeObject = treeObjectStack.pop();

			if (currTreeObject.isFile()) {
				TreeObjectPath treePath = currTreeObject.getPath();
				String pathname = treePath.getRootRelativePathname();
				pathMap.put(pathname, currTreeObject);
			}

			Enumeration<TreeObject> children = currTreeObject.children();
			while (children.hasMoreElements()) {
				TreeObject childTreeObject = children.nextElement();
				treeObjectStack.push(childTreeObject);
			}
		}
		return pathMap;
	}

	@Override
	public int compareTo(TreeObject o) {
		return getSize().compareTo(o.getSize());
	}
}
