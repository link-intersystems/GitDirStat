package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.ObjectId;

public class TreeObjectBuilderTreeEntryWalk implements TreeEntryWalk {

	private TreeObject root;

	public TreeObjectBuilderTreeEntryWalk(TreeObject root) {
		this.root = root;
	}

	@Override
	public void walk(TreeEntry treeEntry) throws IOException {
		String pathString = treeEntry.getPathString();
		ObjectId objectId = treeEntry.getObjectId();
		long size = treeEntry.getSize();
		TreeObject treeObject = root.makePath(pathString);
		ObjectSize objectSize = new ObjectSize(objectId, size);
		treeObject.addObjectSize(objectSize);
	}

}
