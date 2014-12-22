package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

public class TreeUpdateImpl implements TreeUpdate {

	private TreeWalk tw;
	private TreeFileUpdateImpl actualTreeFile;
	private Collection<TreeFileUpdateImpl> treeFiles = new ArrayList<TreeFileUpdateImpl>();
	private RevTree tree;

	TreeUpdateImpl(CommitUpdateImpl commitUpdate, GitRepository gitRepository,
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

			for (TreeFileUpdateImpl treeFile : treeFiles) {
				treeFile.apply(builder);
			}

			builder.commit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#hasNext()
	 */
	@Override
	public boolean hasNext() {
		try {
			return tw.next();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#next()
	 */
	@Override
	public TreeFileUpdateImpl next() {
		CanonicalTreeParser tree = tw.getTree(0, CanonicalTreeParser.class);
		byte[] rawPath = tw.getRawPath();
		String pathString = tw.getPathString();
		FileMode entryFileMode = tree.getEntryFileMode();
		ObjectId entryObjectId = tree.getEntryObjectId();
		actualTreeFile = new TreeFileUpdateImpl(entryFileMode, entryObjectId,
				rawPath, pathString);
		treeFiles.add(actualTreeFile);
		return actualTreeFile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.link_intersystems.tools.git.domain.ITreeUpdate#remove()
	 */
	@Override
	public void remove() {
		if (actualTreeFile != null) {
			actualTreeFile.delete();
		}
	}

}