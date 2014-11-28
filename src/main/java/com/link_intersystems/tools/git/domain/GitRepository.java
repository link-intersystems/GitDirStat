package com.link_intersystems.tools.git.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.ObjectWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.StopAtRevFilter;
import com.link_intersystems.tools.git.common.NullProgressMonitor;
import com.link_intersystems.tools.git.common.ProgressMonitor;

public class GitRepository {

	private Repository repository;

	public GitRepository(Repository repository) {
		this.repository = repository;
	}

	static String createId(File repositoryDirectory) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.readEnvironment();
		builder.findGitDir(repositoryDirectory);
		File gitDir = builder.getGitDir();
		try {
			return gitDir.getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getId() {
		try {
			return getRepository().getDirectory().getCanonicalPath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public Repository getRepository() {
		return repository;
	}

	public CommitRange getCommitRange(String revstr) throws IOException {
		Repository repository = getRepository();
		AnyObjectId toInclusive = repository.resolve(revstr);
		AnyObjectId fromInclusive = getInitialCommit(toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	private RevCommit getInitialCommit(AnyObjectId headId) throws IOException {
		Repository repository = getRepository();
		RevWalk rw = new RevWalk(repository);
		RevCommit c = null;
		RevCommit root = rw.parseCommit(headId);
		rw.sort(RevSort.REVERSE);
		rw.markStart(root);
		c = rw.next();
		return c;
	}

	public TreeObject getCommitRangeTree(CommitRange commitRange,
			ProgressMonitor progressMonitor) {
		try {
			ObjectWalk objectWalk = createObjectWalk(repository, commitRange);

			ObjectDatabase objectDatabase = repository.getObjectDatabase();
			ObjectReader objectReader = objectDatabase.newReader();

			List<ObjectId> treeIds = getTreeIds(objectWalk);

			CommitRangeTree root = new CommitRangeTree(commitRange);

			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			for (ObjectId treeId : treeIds) {
				treeWalk.addTree(treeId);
			}

			progressMonitor.start(treeIds.size());

			while (treeWalk.next()) {
				String pathString = treeWalk.getPathString();
				ObjectId objectId = treeWalk.getObjectId(0);
				if (ObjectId.zeroId().equals(objectId)) {
					continue;
				}
				long size = objectReader.getObjectSize(objectId,
						ObjectReader.OBJ_ANY);

				TreeObject treeObject = root.makePath(pathString);
				ObjectSize objectSize = new ObjectSize(objectId, size);
				treeObject.addObjectSize(objectSize);
				progressMonitor.update(1);
			}

			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			progressMonitor.end();
		}
	}

	private ObjectWalk createObjectWalk(Repository repository,
			CommitRange commitRange) throws AmbiguousObjectException,
			IncorrectObjectTypeException, IOException, MissingObjectException {
		ObjectWalk objectWalk = new ObjectWalk(repository);
		AnyObjectId toInclusive = commitRange.getToInclusive();
		RevCommit revCommit = objectWalk.parseCommit(toInclusive);
		objectWalk.markStart(revCommit);
		AnyObjectId fromInclusive = commitRange.getFromInclusive();
		StopAtRevFilter stopAtRevFilter = new StopAtRevFilter(fromInclusive);
		objectWalk.setRevFilter(stopAtRevFilter);
		return objectWalk;
	}

	private List<ObjectId> getTreeIds(ObjectWalk objectWalk)
			throws MissingObjectException, IncorrectObjectTypeException,
			IOException {
		List<ObjectId> treeIds = new ArrayList<ObjectId>();

		while ((objectWalk.next()) != null) {
			RevObject nextObject = objectWalk.nextObject();
			if (nextObject instanceof RevTree) {
				RevTree revTree = (RevTree) nextObject;
				ObjectId treeId = revTree.getId();
				treeIds.add(treeId);
			}
		}
		return treeIds;
	}

	public TreeObject getCommitRangeTree(CommitRange commitRange) {
		return getCommitRangeTree(commitRange, NullProgressMonitor.INSTANCE);
	}

}
