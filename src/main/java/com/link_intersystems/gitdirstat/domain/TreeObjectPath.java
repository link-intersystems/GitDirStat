package com.link_intersystems.gitdirstat.domain;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.iterators.IteratorEnumeration;

public class TreeObjectPath {

	private static final char PATH_SEPARATOR = '/';
	private List<TreeObject> path = new ArrayList<TreeObject>();

	TreeObjectPath(TreeObject treeObject) {
		path.add(treeObject);
	}

	void prepend(TreeObjectPath otherPath) {
		this.path.addAll(0, otherPath.path);
	}

	public String getName() {
		TreeObject lastPathSegment = this.path.get(this.path.size() - 1);
		return lastPathSegment.getName();
	}

	public String getPathname() {
		StringBuilder pathname = new StringBuilder();
		Iterator<TreeObject> pathIterator = this.path.iterator();
		while (pathIterator.hasNext()) {
			TreeObject treeObject = pathIterator.next();
			pathname.append(treeObject.getName());
			if (pathIterator.hasNext()) {
				pathname.append(PATH_SEPARATOR);
			}
		}
		return pathname.toString();
	}

	public String getRootRelativePathname() {
		StringBuilder pathname = new StringBuilder();
		Iterator<TreeObject> pathIterator = this.path.iterator();
		if(pathIterator.hasNext()){
			pathIterator.next();
		}

		while (pathIterator.hasNext()) {
			TreeObject treeObject = pathIterator.next();

			pathname.append(treeObject.getName());
			if (pathIterator.hasNext()) {
				pathname.append(PATH_SEPARATOR);
			}
		}
		return pathname.toString();
	}

	public Enumeration<TreeObject> enumerate() {
		return new IteratorEnumeration<TreeObject>(path.iterator());
	}

	@Override
	public String toString() {
		return getRootRelativePathname();
	}
}
