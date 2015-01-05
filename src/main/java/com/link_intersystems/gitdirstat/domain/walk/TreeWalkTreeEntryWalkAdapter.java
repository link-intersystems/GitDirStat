package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;

import org.apache.commons.collections4.Predicate;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import com.link_intersystems.gitdirstat.domain.TreeEntryWalk;
import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry;

public class TreeWalkTreeEntryWalkAdapter implements RevCommitWalk {

	private TreeEntryWalk treeEntryWalk;

	private TreeWalk treeWalk;
	private TreeWalkTreeEntry treeWalkTreeEntry;

	public TreeWalkTreeEntryWalkAdapter(ObjectReader objectReader,
			TreeEntryWalk treeEntryWalk) {
		this.treeEntryWalk = treeEntryWalk;
		treeWalk = new TreeWalk(objectReader);
		treeWalk.setRecursive(true);
		treeWalkTreeEntry = new TreeWalkTreeEntry(treeWalk);
	}

	@Override
	public void walk(RevCommit revCommit) throws IOException {
		treeWalk.reset(revCommit.getTree());

		while (treeWalk.next()) {
			treeEntryWalk.walk(treeWalkTreeEntry);
		}
	}

	public void setTreeWalkFilter(final Predicate<TreeEntry> filterPredicate) {
		TreeFilter treeFilter = null;
		if (filterPredicate != null) {
			treeFilter = new TreeWalkFilterPredicateAdapter(filterPredicate,
					treeWalkTreeEntry);
		}
		treeWalk.setFilter(treeFilter);
	}

	static class TreeWalkFilterPredicateAdapter extends TreeFilter {

		private TreeEntry treeEntry;
		private Predicate<TreeEntry> filterPredicate;

		public TreeWalkFilterPredicateAdapter(
				Predicate<TreeEntry> filterPredicate, TreeEntry treeEntry) {
			this.filterPredicate = filterPredicate;
			this.treeEntry = treeEntry;
		}

		@Override
		public boolean include(TreeWalk walker) throws MissingObjectException,
				IncorrectObjectTypeException, IOException {
			return !filterPredicate.evaluate(treeEntry);
		}

		@Override
		public boolean shouldBeRecursive() {
			return true;
		}

		@Override
		public TreeFilter clone() {
			return this;
		}

	}

	static class TreeWalkTreeEntry implements TreeEntry {

		private TreeWalk treeWalk;
		private ObjectReader objectReader;

		public TreeWalkTreeEntry(TreeWalk treeWalk) {
			this.treeWalk = treeWalk;
			this.objectReader = treeWalk.getObjectReader();
		}

		public boolean isTree() {
			return FileMode.TREE.equals(getFileMode());
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

		@Override
		public byte[] getRawPath() {
			return treeWalk.getRawPath();
		}

		@Override
		public TreeEntryEquality getEqualityObject() {
			return new DefaultTreeEntryEquality(getObjectId(), getRawPath());
		}
	}

}
