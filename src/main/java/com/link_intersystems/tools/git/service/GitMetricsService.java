package com.link_intersystems.tools.git.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.comparators.ReverseComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
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
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.StopAtRevFilter;

public class GitMetricsService {
	public static enum SortOrder {
		ASC, DESC;
	}

	private Repository repository;

	public GitMetricsService(Repository repository) {
		this.repository = repository;
	}

	public SizeMetrics getSizeMetrics(CommitRange commitRange)
			throws GitMetricsException {
		return getSizeMetrics(commitRange, SortOrder.ASC);

	}

	public SizeMetrics getSizeMetrics(CommitRange commitRange,
			SortOrder sortOrder) throws GitMetricsException {
		try {
			ObjectWalk objectWalk = createObjectWalk(commitRange);

			ObjectDatabase objectDatabase = repository.getObjectDatabase();
			ObjectReader objectReader = objectDatabase.newReader();

			List<ObjectId> treeIds = getTreeIds(objectWalk);

			Map<String, BigInteger> pathSizes = getPathSizes(repository,
					objectReader, treeIds);

			Map<String, BigInteger> sorted = sortByValue(pathSizes, sortOrder);

			return new SizeMetrics(sorted);
		} catch (Exception e) {
			throw new GitMetricsException(e);
		}
	}

	private ObjectWalk createObjectWalk(CommitRange commitRange)
			throws AmbiguousObjectException, IncorrectObjectTypeException,
			IOException, MissingObjectException {
		ObjectWalk objectWalk = new ObjectWalk(repository);
		RevCommit revCommit = objectWalk.parseCommit(commitRange
				.getToInclusive());
		objectWalk.markStart(revCommit);
		AnyObjectId fromInclusive = commitRange.getFromInclusive();
		StopAtRevFilter stopAtRevFilter = new StopAtRevFilter(fromInclusive);
		objectWalk.setRevFilter(stopAtRevFilter);
		return objectWalk;
	}

	private Map<String, BigInteger> getPathSizes(Repository repository,
			ObjectReader objectReader, List<ObjectId> treeIds)
			throws MissingObjectException, IncorrectObjectTypeException,
			CorruptObjectException, IOException {
		Map<String, BigInteger> path2Size = new HashMap<String, BigInteger>();
		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.setRecursive(true);
		for (ObjectId treeId : treeIds) {
			treeWalk.addTree(treeId);

		}
		while (treeWalk.next()) {
			String pathString = treeWalk.getPathString();
			ObjectId objectId = treeWalk.getObjectId(0);
			if (ObjectId.zeroId().equals(objectId)) {
				continue;
			}
			long size = objectReader.getObjectSize(objectId,
					ObjectReader.OBJ_ANY);
			BigInteger bigInteger = path2Size.get(pathString);
			if (bigInteger == null) {
				bigInteger = BigInteger.ZERO;
			}
			bigInteger = bigInteger.add(BigInteger.valueOf(size));
			path2Size.put(pathString, bigInteger);
		}
		return path2Size;
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

	private static <K, V> Map<K, V> sortByValue(Map<K, V> map,
			SortOrder sortOrder) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Comparator<Map.Entry<K, V>> c = new Comparator<Map.Entry<K, V>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return ((Comparable) (o1).getValue())
						.compareTo((o2).getValue());
			}
		};

		if (SortOrder.DESC.equals(sortOrder)) {
			c = new ReverseComparator<Map.Entry<K, V>>(c);
		}

		Collections.sort(list, c);

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}
