package com.link_intersystems.junit.jgit;

import org.eclipse.jgit.treewalk.TreeWalk;

public class ActualTree {

	private TreeWalk treeWalk;

	public ActualTree(TreeWalk treeWalk) {
		this.treeWalk = treeWalk;
	}

	public String getPath() {
		return treeWalk.getPathString();
	}

}
