package com.link_intersystems.junit.jgit;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Assert;

import com.link_intersystems.gitdirstat.CommitTreeAssertion;

public class RepoAssertion {

	private Repository repository;

	public RepoAssertion(Repository repository) {
		this.repository = repository;
	}

	public ActualCommit getCommitAssertion(String revstr) throws Exception {
		ObjectId commitId = repository.resolve(revstr);
		Assert.assertNotNull("Commit " + revstr + " does not exist", commitId);
		RevWalk walk = new RevWalk(repository);
		RevCommit revCommit = walk.parseCommit(commitId);

		return new ActualCommit(revCommit);
	}

	public void assertThatAllCommits(CommitSelection commitSelection,
			CommitAssertion commitAssertion) throws Exception {

		RevWalk revWalk = createAllCommitsRevWalk(commitSelection);

		for (RevCommit revCommit : revWalk) {
			ActualCommit actualCommit = new ActualCommit(revCommit);
			commitAssertion.assertCommit(actualCommit);
		}
	}

	public void assertCommitTrees(CommitSelection commitSelection,
			CommitTreeAssertion commitTreeAssertion) throws Exception {
		RevWalk revWalk = createAllCommitsRevWalk(commitSelection);

		for (RevCommit revCommit : revWalk) {
			ActualCommit actualCommit = new ActualCommit(revCommit);
			TreeWalk treeWalk = new TreeWalk(repository);
			RevTree revTree = revCommit.getTree();
			treeWalk.addTree(revTree);
			ActualTree actualTree = new ActualTree(treeWalk);
			while (treeWalk.next()) {
				commitTreeAssertion.assertTree(actualCommit, actualTree);
			}
		}
	}

	private RevWalk createAllCommitsRevWalk(CommitSelection commitSelection)
			throws IOException, MissingObjectException,
			IncorrectObjectTypeException {
		RevWalk revWalk = new RevWalk(repository);
		RefDatabase refDatabase = repository.getRefDatabase();
		Map<String, org.eclipse.jgit.lib.Ref> refs = refDatabase.getRefs("");
		for (Entry<String, Ref> entryRef : refs.entrySet()) {
			ObjectId refObject = entryRef.getValue().getObjectId();
			RevObject revObject = revWalk.parseAny(refObject);
			if (revObject instanceof RevCommit) {
				revWalk.markStart((RevCommit) revObject);
			}
		}
		revWalk.setRevFilter(new CommitSelectionRevFilter(commitSelection));
		revWalk.sort(RevSort.REVERSE);
		revWalk.sort(RevSort.TOPO, true);
		return revWalk;
	}
}
