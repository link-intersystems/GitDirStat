package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.link_intersystems.tools.git.domain.TreeEntryWalk.TreeEntry;

public class TreeWalkTreeEntryWalkAdapter implements RevCommitWalk {

	private TreeEntryWalk treeEntryWalk;

	private TreeWalk treeWalk;
	private TreeWalkTreeEntry treeWalkTreeEntry;
	private Predicate<TreeEntry> filterPredicate = FalsePredicate
			.falsePredicate();

	public TreeWalkTreeEntryWalkAdapter(ObjectReader objectReader,
			TreeEntryWalk treeEntryWalk) {
		this.treeEntryWalk = treeEntryWalk;
		treeWalk = new TreeWalk(objectReader);
		treeWalk.setRecursive(true);
		treeWalk.setPostOrderTraversal(true);
		treeWalkTreeEntry = new TreeWalkTreeEntry(treeWalk);
	}

	@Override
	public void walk(RevCommit revCommit) throws IOException {
		treeWalk.reset(revCommit.getTree());

		while (treeWalk.next()) {
			if (!filterPredicate.evaluate(treeWalkTreeEntry)) {
				treeEntryWalk.walk(treeWalkTreeEntry);
			}
		}
	}

	public void setTreeWalkFilter(Predicate<TreeEntry> filterPredicate) {
		if (filterPredicate == null) {
			filterPredicate = FalsePredicate.falsePredicate();
		}
		this.filterPredicate = filterPredicate;
	}

	private static class TreeWalkTreeEntry implements TreeEntry {

		private TreeWalk treeWalk;
		private ObjectReader objectReader;

		public TreeWalkTreeEntry(TreeWalk treeWalk) {
			this.treeWalk = treeWalk;
			this.objectReader = treeWalk.getObjectReader();
		}

		@Override
		public FileMode getFileMode() {
			return treeWalk.getFileMode(0);
		}

		@Override
		public String getPathString() {
			return treeWalk.getPathString();
		}

		@Override
		public ObjectId getObjectId() {
			return treeWalk.getObjectId(0);
		}

		@Override
		public long getSize() throws IOException {
			long objectSize = objectReader.getObjectSize(getObjectId(),
					Constants.OBJ_BLOB);
			return objectSize;
		}

	}

}
