package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectDatabase;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.ObjectWalk;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {

		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.readEnvironment()
				.findGitDir(gitRepositoryDir).build();
		ObjectWalk objectWalk = new ObjectWalk(repository);
		ObjectId headCommitId = repository.resolve(Constants.HEAD);
		RevCommit revCommit = objectWalk.parseCommit(headCommitId);
		objectWalk.markStart(revCommit);

		ObjectDatabase objectDatabase = repository.getObjectDatabase();
		ObjectReader objectReader = objectDatabase.newReader();

		Map<String, BigInteger> path2Size = new HashMap<String, BigInteger>();

		RevCommit next = objectWalk.next();
		while (next != null) {
			RevObject nextObject = objectWalk.nextObject();
			if (nextObject instanceof RevTree) {
				RevTree revTree = (RevTree) nextObject;
				TreeWalk treeWalk = new TreeWalk(repository);
				treeWalk.setRecursive(true);
				treeWalk.addTree(revTree.getId());
				while (treeWalk.next()) {
					String pathString = treeWalk.getPathString();
					ObjectId objectId = treeWalk.getObjectId(0);
					long size = objectReader.getObjectSize(objectId, ObjectReader.OBJ_ANY);
					BigInteger bigInteger = path2Size.get(pathString);
					if (bigInteger == null) {
						bigInteger = BigInteger.ZERO;
					}
					bigInteger = bigInteger.add(BigInteger.valueOf(size));
					path2Size.put(pathString, bigInteger);
				}
			}
			next = objectWalk.next();
		}
		Map<String, BigInteger> sorted = sortByValue(path2Size);

		for (Entry<String, BigInteger> entry : sorted.entrySet()) {
			System.out.println(StringUtils.rightPad(entry.getValue().toString(), 30) + " " + entry.getKey());
		}
	}

	private static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@SuppressWarnings("unchecked")
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return ((Comparable) (o1).getValue()).compareTo((o2).getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
