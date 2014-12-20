package com.link_intersystems.tools.git.domain;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.ObjectWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.link_intersystems.tools.git.CommitRange;

public class GitRepository {

	private Repository repository;
	private RefFactory refFactory;

	private CommitAccess commitAccess = new CommitAccess();

	public GitRepository(Repository repository) {
		this.repository = repository;
		refFactory = new RefFactory(this);
	}

	public GitRepository(Git git) {
		this(git.getRepository());
	}

	static String createId(File repositoryDirectory) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.readEnvironment();
		builder.findGitDir(repositoryDirectory);
		File gitDir = builder.getGitDir();
		if (gitDir == null) {
			String message = MessageFormat.format(
					"No git repository found at {0}", repositoryDirectory);
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

		List<T> refList = new ArrayList<T>();
		for (String prefix : prefixes) {
			try {
				Map<String, org.eclipse.jgit.lib.Ref> refs = refDatabase
						.getRefs(prefix);
				for (Entry<String, org.eclipse.jgit.lib.Ref> refEntry : refs
						.entrySet()) {
					org.eclipse.jgit.lib.Ref jgitRef = refEntry.getValue();
					Ref ref = refFactory.create(jgitRef);
					if (ref != null) {
						refList.add((T) ref);
					}

				}
			} catch (IOException e) {
				throw new GitRepositoryException(e);
			}

		}

		Collections.sort(refList, DefaultRefSorter.INSTANCE);

		return refList;
	}

	public CommitRange getCommitRange(String revstr) throws IOException {
		Repository repository = getRepository();
		AnyObjectId toInclusive = repository.resolve(revstr);
		AnyObjectId fromInclusive = getInitialCommit(toInclusive);
		CommitRange revRange = new CommitRange(fromInclusive, toInclusive);
		return revRange;
	}

	public Collection<CommitRange> getCommitRanges(List<? extends Ref> refs,
			ProgressListener progressListener) throws IOException {
		progressListener.start(refs.size());
		try {
			Collection<CommitRange> commitRanges = new ArrayList<CommitRange>();
			for (Ref ref : refs) {
				if (progressListener.isCanceled()) {
					commitRanges = Collections.emptyList();
					break;
				}
				AnyObjectId toInclusive = ref.getCommitId();
				AnyObjectId fromInclusive = getInitialCommit(toInclusive);
				CommitRange revRange = new CommitRange(fromInclusive,
						toInclusive);
				commitRanges.add(revRange);
				progressListener.update(1);
			}
			return commitRanges;
		} finally {
			progressListener.end();
		}

	}

	public Collection<CommitRange> getCommitRanges(List<? extends Ref> refs)
			throws IOException {
		return getCommitRanges(refs, new NullProgressListener());
	}

	private RevCommit getInitialCommit(AnyObjectId headId) throws IOException {
		Repository repository = getRepository();
		RevWalk rw = new RevWalk(repository);
		RevCommit c = null;
		RevCommit root = rw.parseCommit(headId);
		rw.markStart(root);
		rw.sort(RevSort.REVERSE);
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
			if (commitRanges.isEmpty()) {
				return root;
			}

			int totalWork = getTotalWork(commitRanges);

			RevWalk revWalk = createRevWalk(commitRanges);

			ObjectDatabase objectDatabase = repository.getObjectDatabase();
			ObjectReader objectReader = objectDatabase.newReader();

			progressListener.start(totalWork);
			Predicate<String> uniqueTreeObjects = UniquePredicate
					.uniquePredicate();

			for (RevCommit revCommit : revWalk) {
				if (progressListener.isCanceled()) {
					root = new CommitRangeTree(getId(), commitRanges);
					break;
				}

				TreeWalk treeWalk = new TreeWalk(repository);
				treeWalk.setRecursive(true);
				RevTree tree = revCommit.getTree();
				treeWalk.addTree(tree);
				while (treeWalk.next()) {
					String treeWalkEntryUniqueKey = createTreeWalkEntryUniqueKey(treeWalk);
					if (uniqueTreeObjects.evaluate(treeWalkEntryUniqueKey)) {
						ObjectId currentId = treeWalk.getObjectId(0);

						long size = objectReader.getObjectSize(currentId,
								Constants.OBJ_BLOB);

						String pathString = treeWalk.getPathString();
						TreeObject treeObject = root.makePath(pathString);
						ObjectSize objectSize = new ObjectSize(currentId, size);
						treeObject.addObjectSize(objectSize);
					}
				}
				treeWalk.release();
				progressListener.update(1);
			}
			return root;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			progressListener.end();
		}
	}

	private String createTreeWalkEntryUniqueKey(TreeWalk treeWalk) {
		String pathString = treeWalk.getPathString();
		ObjectId currentId = treeWalk.getObjectId(0);
		return currentId.name() + pathString;
	}

	ObjectWalk createObjectWalk(Collection<CommitRange> commitRanges)
			throws IOException {
		ObjectWalk objectWalk = new ObjectWalk(repository);
		applyCommitRanges(objectWalk, commitRanges);
		return objectWalk;
	}

	private void applyCommitRanges(RevWalk revWalk,
			Collection<CommitRange> commitRanges)
			throws MissingObjectException, IncorrectObjectTypeException,
			IOException {
		for (CommitRange commitRange : commitRanges) {
			AnyObjectId fromInclusive = commitRange.getToInclusive();
			RevCommit revCommit = revWalk.parseCommit(fromInclusive);
			revWalk.markStart(revCommit);
		}
	}

	private CommitWalk createCommitWalk(Collection<CommitRange> commitRanges)
			throws IOException {
		RevWalk revWalk = createRevWalk(commitRanges);

		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);

		CommitWalk commitWalk = new CommitWalk(revWalk, commitAccess);
		return commitWalk;
	}

	RevWalk createRevWalk(Collection<CommitRange> commitRanges)
			throws IOException {
		RevWalk revWalk = new RevWalk(repository);
		applyCommitRanges(revWalk, commitRanges);
		return revWalk;
	}

	public TreeObject getCommitRangeTree(CommitRange commitRange) {
		return getCommitRangeTree(commitRange, NullProgressListener.INSTANCE);
	}

	public TreeObject getCommitRangeTree(List<? extends Ref> selectedRefs,
			ProgressListener progressListener) throws IOException {
		progressListener.start(1000);
		try {
			Collection<CommitRange> commitRanges = getCommitRanges(
					selectedRefs,
					new SubProgressListener(progressListener, 200));
			return getCommitRangeTree(commitRanges, new SubProgressListener(
					progressListener, 800));
		} finally {
			progressListener.end();
		}
	}

	public void applyFilter(Collection<CommitRange> commitRanges,
			IndexFilter indexFilter, ProgressListener progressListener)
			throws IOException, CheckoutConflictException, GitAPIException {
		Git git = getGit();
		BranchMemento currentBranchMemento = new BranchMemento(git);
		currentBranchMemento.save();

		int totalWork = getTotalWork(commitRanges);
		CommitWalk commitWalk = createCommitWalk(commitRanges);
		RewriteIndexCommitWalkIterator rewriteIterator = new RewriteIndexCommitWalkIterator(
				git, commitWalk);

		HistoryUpdate historyUpdate = new HistoryUpdate(this);

		progressListener.start(totalWork);
		while (rewriteIterator.hasNext()) {
			Commit commit = rewriteIterator.next();

			CacheCommitUpdate commitUpdate = new CacheCommitUpdate(this,
					commit, historyUpdate);
			commitUpdate.beginUpdate();

			indexFilter.apply(commitUpdate);
			commitUpdate.execute();

			commitUpdate.endUpdate();

			progressListener.update(1);
			if (progressListener.isCanceled()) {
				break;
			}
		}

		if (!progressListener.isCanceled()) {
			historyUpdate.updateRefs();
			pruneObjectsNow();
		}
		currentBranchMemento.restore();

		rewriteIterator.close();
		progressListener.end();
	}

	private void pruneObjectsNow() throws GitAPIException {
		ExpireReflogCommand expireReflogCommand = new ExpireReflogCommand(this);
		expireReflogCommand.call();

		Git git = getGit();
		GarbageCollectCommand gc = git.gc();
		gc.setExpire(null);
		gc.call();
	}

	private int getTotalWork(Collection<CommitRange> commitRanges)
			throws IOException {
		RevWalk revWalk = createRevWalk(commitRanges);

		revWalk.sort(RevSort.TOPO);
		revWalk.sort(RevSort.REVERSE, true);

		int total = 0;
		while (revWalk.next() != null) {
			total++;
		}
		return total;
	}

	public Git getGit() {
		return new Git(getRepository());
	}

	public void applyFilter(Collection<CommitRange> commitRanges,
			IndexFilter indexFilter) throws IOException,
			CheckoutConflictException, GitAPIException {
		applyFilter(commitRanges, indexFilter, NullProgressListener.INSTANCE);
	}

	public void applyFilter(IndexFilter indexFilter) throws IOException,
			GitAPIException {
		List<Ref> refs = getRefs(Ref.class);
		applyFilter(refs, indexFilter, NullProgressListener.INSTANCE);
	}

	public void applyFilter(IndexFilter indexFilter,
			ProgressListener progressListener) throws IOException,
			GitAPIException {
		List<Ref> refs = getRefs(Ref.class);
		applyFilter(refs, indexFilter, progressListener);
	}

	public void applyFilter(List<? extends Ref> refs, IndexFilter indexFilter)
			throws IOException, GitAPIException {
		Collection<CommitRange> commitRanges = getCommitRanges(refs);
		applyFilter(commitRanges, indexFilter, NullProgressListener.INSTANCE);
	}

	public void applyFilter(List<? extends Ref> refs, IndexFilter indexFilter,
			ProgressListener progressListener) throws IOException,
			GitAPIException {
		Collection<CommitRange> commitRanges = getCommitRanges(refs);
		applyFilter(commitRanges, indexFilter, progressListener);
	}

	CommitAccess getCommitAccess() {
		return commitAccess;
	}

}
