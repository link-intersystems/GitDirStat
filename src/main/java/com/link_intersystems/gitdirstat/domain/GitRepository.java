package com.link_intersystems.gitdirstat.domain;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.link_intersystems.gitdirstat.domain.walk.CommitRangeTreeBuilder;
import com.link_intersystems.gitdirstat.domain.walk.CommitWalker;
import com.link_intersystems.gitdirstat.domain.walk.RevWalkCommitRangeTreeBuilder;

public class GitRepository {

	private Repository repository;
	private RefFactory refFactory;

	private ObjectDatabase objectDatabase;
	private ObjectReader objectReader;

	private CommitDatabase commitDatabase = new CommitDatabase(this);

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
		ObjectId toInclusive = repository.resolve(revstr);
		ObjectId fromInclusive = getInitialCommit(toInclusive);
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
				ObjectId toInclusive = ref.getCommitId();
				if (toInclusive != null) {
					CommitRange revRange = new CommitRange(null, toInclusive);
					commitRanges.add(revRange);
				}
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

		CommitRangeTreeBuilder commitRangeTreeBuilder = new RevWalkCommitRangeTreeBuilder(
				this);
		return commitRangeTreeBuilder.build(commitRanges, progressListener);
	}

	private CommitWalker createCommitWalker(Collection<CommitRange> commitRanges)
			throws IOException {
		CommitWalker commitWalk = new CommitWalker(this);
		commitWalk.setCommitRanges(commitRanges);
		commitWalk.sort(RevSort.TOPO, RevSort.REVERSE);
		return commitWalk;
	}

	public TreeObject getCommitRangeTree(CommitRange commitRange) {
		return getCommitRangeTree(commitRange, NullProgressListener.INSTANCE);
	}

	public TreeObject getCommitRangeTree(List<? extends Ref> selectedRefs,
			ProgressListener progressListener) throws IOException {
		progressListener.start(1000);
		try {
			Collection<CommitRange> commitRanges = getCommitRanges(selectedRefs);
			return getCommitRangeTree(commitRanges, progressListener);
		} finally {
			progressListener.end();
		}
	}

	public void applyFilter(Collection<CommitRange> commitRanges,
			IndexFilter indexFilter, ProgressListener progressListener)
			throws IOException, GitAPIException {
		HistoryUpdate historyUpdate = new HistoryUpdate(this);
		IndexUpdate indexUpdate = historyUpdate.begin();

		CommitWalker commitWalk = createCommitWalker(commitRanges);
		int totalWork = commitWalk.getWalkCount();

		Iterator<Commit> commitIterator = commitWalk.iterator();

		try {
			progressListener.start(totalWork);

			while (commitIterator.hasNext()) {
				Commit commit = commitIterator.next();

				CacheCommitUpdate commitUpdate = indexUpdate
						.beginUpdate(commit);

				indexFilter.apply(commitUpdate);
				commitUpdate.writeCommit();
				commitUpdate.end();

				progressListener.update(1);
				if (progressListener.isCanceled()) {
					break;
				}
			}
			commitWalk.close();

			if (!progressListener.isCanceled()) {
				applyHistoryUpdate(historyUpdate);
			}
		} finally {
			try {
				historyUpdate.close();
			} catch (GitAPIException e) {
			}
			progressListener.end();
		}

	}

	private void applyHistoryUpdate(HistoryUpdate historyUpdate) throws IOException,
			GitAPIException {
		historyUpdate.updateRefs();
		historyUpdate.cleanupRepository();
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

	CommitDatabase getCommitAccess() {
		return commitDatabase;
	}

	public Commit getCommit(RevCommit revCommit) {
		return getCommitAccess().getCommit(revCommit);
	}

	ObjectDatabase getObjectDatabase() {
		if (objectDatabase == null) {
			objectDatabase = repository.getObjectDatabase();
		}
		return objectDatabase;
	}

	public ObjectReader getObjectReader() {
		if (objectReader == null) {
			objectReader = getObjectDatabase().newReader();
		}
		return objectReader;
	}

	public void release() {
		if (objectReader != null) {
			objectReader.release();
		}
		repository.close();
	}

	public void getRefs(org.eclipse.jgit.lib.Ref jgitRef) {
		// TODO Auto-generated method stub

	}

}
