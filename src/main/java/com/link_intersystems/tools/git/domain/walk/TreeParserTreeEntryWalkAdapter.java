package com.link_intersystems.tools.git.domain.walk;

import java.io.IOException;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import com.link_intersystems.tools.git.domain.TreeEntryWalk;
import com.link_intersystems.tools.git.domain.TreeEntryWalk.TreeEntry;

public class TreeParserTreeEntryWalkAdapter implements RevCommitWalk {

	private ObjectReader objectReader;
	private CanonicalTreeParser treeParser;
	private TreeParserTreeEntry treeParserEntry;

	private TreeEntryWalk treeEntryWalk;
	private Predicate<TreeEntry> filterPredicate = FalsePredicate
			.falsePredicate();

	public TreeParserTreeEntryWalkAdapter(ObjectReader objectReader,
			TreeEntryWalk treeEntryWalk) {
		this.objectReader = objectReader;
		this.treeEntryWalk = treeEntryWalk;
		treeParser = new CanonicalTreeParser();
		treeParserEntry = new TreeParserTreeEntry(treeParser, objectReader);
	}

	@Override
	public void walk(RevCommit revCommit) throws IOException {
		RevTree tree = revCommit.getTree();
		treeParser.reset(objectReader, tree);

		while (!treeParser.eof()) {
			treeParser.next();
			if (!filterPredicate.evaluate(treeParserEntry)) {
				treeEntryWalk.walk(treeParserEntry);
			}
		}
	}

	public void setTreeWalkFilter(Predicate<TreeEntry> filterPredicate) {
		if (filterPredicate == null) {
			filterPredicate = FalsePredicate.falsePredicate();
		}
		this.filterPredicate = filterPredicate;
	}

	private static class TreeParserTreeEntry implements TreeEntry {

		private AbstractTreeIterator abstractTreeIterator;
		private ObjectReader objectReader;

		public TreeParserTreeEntry(AbstractTreeIterator abstractTreeIterator,
				ObjectReader objectReader) {
			this.abstractTreeIterator = abstractTreeIterator;
			this.objectReader = objectReader;
		}

		@Override
		public FileMode getFileMode() {
			return abstractTreeIterator.getEntryFileMode();
		}

		@Override
		public String getPathString() {
			return abstractTreeIterator.getEntryPathString();
		}

		@Override
		public ObjectId getObjectId() {
			return abstractTreeIterator.getEntryObjectId();
		}

		@Override
		public long getSize() throws IOException {
			long objectSize = objectReader.getObjectSize(getObjectId(),
					Constants.OBJ_BLOB);
			return objectSize;
		}

	}
}
