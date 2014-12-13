package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

public class TreeUpdate implements Iterator<TreeFileUpdate> {

	private TreeWalk tw;
	private TreeFileUpdate actualTreeFile;
	private Collection<TreeFileUpdate> treeFiles = new ArrayList<TreeFileUpdate>();
	private RevTree tree;

	TreeUpdate(CommitUpdate commitUpdate, GitRepository gitRepository,
			TreeFilter treeFilter) throws IOException {
		tw = new TreeWalk(gitRepository.getRepository());
		Commit commit = commitUpdate.getCommit();
		RevCommit revCommit = commit.getRevCommit();
		tree = revCommit.getTree();
		tw.addTree(tree);
		tw.setRecursive(true);
		tw.setFilter(treeFilter);
	}

	void apply(DirCache dirCache) throws IOException {
		try {
			DirCacheBuilder builder = dirCache.builder();

			for (TreeFileUpdate treeFile : treeFiles) {
				treeFile.apply(builder);
			}

			builder.commit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return tw.next();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public TreeFileUpdate next() {
		CanonicalTreeParser tree = tw.getTree(0, CanonicalTreeParser.class);
		byte[] rawPath = tw.getRawPath();
		String pathString = tw.getPathString();
		FileMode entryFileMode = tree.getEntryFileMode();
		ObjectId entryObjectId = tree.getEntryObjectId();
		actualTreeFile = new TreeFileUpdate(entryFileMode, entryObjectId,
				rawPath, pathString);
		treeFiles.add(actualTreeFile);
		return actualTreeFile;
	}

	@Override
	public void remove() {
		if (actualTreeFile != null) {
			actualTreeFile.delete();
		}
	}

}
