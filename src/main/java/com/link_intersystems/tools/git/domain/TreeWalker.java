package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

public class TreeWalker implements Iterable<TreeWalkerEntry> {

	private Commit commit;
	private Collection<TreeWalkerIterator> iterators = new ArrayList<TreeWalker.TreeWalkerIterator>();

	TreeWalker(Commit commit) {
		this.commit = commit;
	}

	public Commit getCommit() {
		return commit;
	}

	public void release() {
		for (TreeWalkerIterator treeWalkerIterator : iterators) {
			treeWalkerIterator.treeWalk.release();
		}
		iterators.clear();
	}

	private static class TreeWalkerIterator implements
			Iterator<TreeWalkerEntry> {

		private TreeWalker treeWalker;
		private TreeWalk treeWalk;
		private Boolean hasNext = null;

		public TreeWalkerIterator(TreeWalk treeWalk, TreeWalker treeWalker) {
			this.treeWalk = treeWalk;
			this.treeWalker = treeWalker;
		}

		@Override
		public boolean hasNext() {
			if (hasNext == null) {
				try {
					hasNext = treeWalk.next();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return hasNext.booleanValue();
		}

		@Override
		public TreeWalkerEntry next() {
			TreeWalkerEntry treeWalkerEntry = null;
			if (hasNext != null && hasNext) {
				String pathString = treeWalk.getPathString();
				ObjectId objectId = treeWalk.getObjectId(0);
				byte[] rawPath = treeWalk.getRawPath();
				treeWalkerEntry = new TreeWalkerEntry(rawPath, pathString, objectId,
						treeWalker.commit.getGitRepository());
				hasNext = null;
			}
			return treeWalkerEntry;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public Iterator<TreeWalkerEntry> iterator() {
		Repository repository = commit.getGitRepository().getRepository();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		RevTree tree = getCommit().getRevCommit().getTree();
		try {
			treeWalk.addTree(tree);
			TreeWalkerIterator treeWalkerIterator = new TreeWalkerIterator(
					treeWalk, this);
			iterators.add(treeWalkerIterator);
			return treeWalkerIterator;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
