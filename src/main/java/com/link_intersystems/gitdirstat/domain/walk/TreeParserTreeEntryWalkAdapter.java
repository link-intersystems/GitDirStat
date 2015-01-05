package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.link_intersystems.gitdirstat.domain.TreeEntryWalk;
import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry;

public class TreeParserTreeEntryWalkAdapter implements RevCommitWalk {

	private ObjectReader objectReader;
	private CanonicalTreeParser treeParser;

	private TreeEntryWalk treeEntryWalk;
	private Predicate<TreeEntry> filterPredicate = FalsePredicate
			.falsePredicate();

	public TreeParserTreeEntryWalkAdapter(ObjectReader objectReader,
			TreeEntryWalk treeEntryWalk) {
		this.objectReader = objectReader;
		this.treeEntryWalk = treeEntryWalk;
		treeParser = new CanonicalTreeParser();
	}

	@Override
	public void walk(RevCommit revCommit) throws IOException {
		RevTree tree = revCommit.getTree();
		treeParser.reset(objectReader, tree);

		processTreeParser(treeParser);
	}

	private void processTreeParser(CanonicalTreeParser treeParser)
			throws IOException {
		TreeParserTreeEntry treeParserEntry = new TreeParserTreeEntry(
				treeParser, objectReader);

		List<CanonicalTreeParser> subTrees = new ArrayList<CanonicalTreeParser>();
		while (!treeParser.eof()) {
			FileMode entryFileMode = treeParser.getEntryFileMode();
			if (FileMode.TREE.equals(entryFileMode)) {
				CanonicalTreeParser canonicalTreeParser = new CanonicalTreeParser();
				canonicalTreeParser.reset(objectReader,
						treeParser.getEntryObjectId());
				subTrees.add(canonicalTreeParser);
			} else {
				if (!filterPredicate.evaluate(treeParserEntry)) {
					treeEntryWalk.walk(treeParserEntry);
				}
			}
			treeParser.next();
		}
		for (CanonicalTreeParser subTreeParser : subTrees) {
			processTreeParser(subTreeParser);
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

		@Override
		public byte[] getRawPath() {
			return abstractTreeIterator.getEntryPathBuffer();
		}

		@Override
		public TreeEntryEquality getEqualityObject() {
			return new DefaultTreeEntryEquality(getObjectId(), getRawPath());
		}

	}
}
