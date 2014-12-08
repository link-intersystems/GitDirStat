package com.link_intersystems.tools.git.domain;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.RefDatabase;
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

public class GitRepository {

	private Repository repository;
	private RefFactory refFactory;

	public GitRepository(Repository repository) {
		this.repository = repository;
		refFactory = new RefFactory(this);
	}

	static String createId(File repositoryDirectory) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.readEnvironment();
		builder.findGitDir(repositoryDirectory);
		File gitDir = builder.getGitDir();
		if(gitDir == null){
			String message = MessageFormat.format("No git repository found at {0}", repositoryDirectory);
			throw new GitRepositoryException(message);
		}
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

	@SuppressWarnings("unchecked")
	public <T extends Ref> List<T> getRefs(Class<T> refType) {
		Repository repository = getRepository();
		RefDatabase refDatabase = repository.getRefDatabase();
		refDatabase.refresh();
		List<String> prefixes = refFactory.getRefPrefixes(refType);

		Map<String, org.eclipse.jgit.lib.Ref> allRefs = new HashMap<String, org.eclipse.jgit.lib.Ref>();
		for (String prefix : prefixes) {
			try {
				Map<String, org.eclipse.jgit.lib.Ref> refs = refDatabase
						.getRefs(prefix);
				for (Entry<String, org.eclipse.jgit.lib.Ref> refEntry: refs.entrySet()) {
					org.eclipse.jgit.lib.Ref ref = refEntry.getValue();
					String name = ref.getName();
					allRefs.put(name, ref);
				}
			} catch (IOException e) {
				throw new GitRepositoryException(e);
			}

		}

		List<T> refs = new ArrayList<T>();
		for (Entry<String, org.eclipse.jgit.lib.Ref> refEntry : allRefs
				.entrySet()) {
			org.eclipse.jgit.lib.Ref jgitRef = refEntry.getValue();
			Ref ref = refFactory.create(jgitRef);
			refs.add((T) ref);
		}

		Collections.sort(refs, new DefaultRefSorter());

		return refs;
	}

	public CommitRange getCommitRange(String revstr) throws IOException {
		Repository repository = getRepository();
		AnyObjectId toInclusive = repository.resolve(revstr);
		AnyObjectId fromInclusive = getInitialCommit(toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	public Collection<CommitRange> getCommitRanges(List<? extends Ref> refs)
			throws IOException {
		Collection<CommitRange> commitRanges = new ArrayList<CommitRange>();
		for (Ref ref : refs) {
			org.eclipse.jgit.lib.Ref jgitRef = ref.getJgitRef();
			AnyObjectId toInclusive = jgitRef.getObjectId();
			AnyObjectId fromInclusive = getInitialCommit(toInclusive);
			CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
			commitRanges.add(revRange);
		}
		return commitRanges;
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
			ProgressListener progressListener) {
		List<CommitRange> singletonList = Collections
				.singletonList(commitRange);
		return getCommitRangeTree(singletonList, progressListener);
	}

	public TreeObject getCommitRangeTree(Collection<CommitRange> commitRanges,
			ProgressListener progressListener) {
		try {
			CommitRangeTree root = new CommitRangeTree(getId(), commitRanges);
			if(commitRanges.isEmpty()){
				return root;
			}

			ObjectWalk objectWalk = createObjectWalk(repository, commitRanges);

			ObjectDatabase objectDatabase = repository.getObjectDatabase();
			ObjectReader objectReader = objectDatabase.newReader();

			List<ObjectId> treeIds = getTreeIds(objectWalk);


			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			for (ObjectId treeId : treeIds) {
				treeWalk.addTree(treeId);
			}

			progressListener.start(treeIds.size());

			while (treeWalk.next()) {
				ObjectId objectId = treeWalk.getObjectId(0);
				if (ObjectId.zeroId().equals(objectId)) {
					continue;
				}
				String pathString = treeWalk.getPathString();

				long size = objectReader.getObjectSize(objectId,
						ObjectReader.OBJ_ANY);

				TreeObject treeObject = root.makePath(pathString);
				ObjectSize objectSize = new ObjectSize(objectId, size);
				treeObject.addObjectSize(objectSize);
				progressListener.update(1);
			}

			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			progressListener.end();
		}
	}

	private ObjectWalk createObjectWalk(Repository repository,
			Collection<CommitRange> commitRanges) throws AmbiguousObjectException,
			IncorrectObjectTypeException, IOException, MissingObjectException {
		ObjectWalk objectWalk = new ObjectWalk(repository);

		Collection<RevCommit> startRevCommits = new HashSet<RevCommit>();
		for (CommitRange commitRange : commitRanges) {
			AnyObjectId toInclusive = commitRange.getToInclusive();
			RevCommit revCommit = objectWalk.parseCommit(toInclusive);
			startRevCommits.add(revCommit);
		}
		objectWalk.markStart(startRevCommits);
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
		return getCommitRangeTree(commitRange, NullProgressListener.INSTANCE);
	}

}
