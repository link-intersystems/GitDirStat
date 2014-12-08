package com.link_intersystems.tools.git.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jgit.dircache.DirCache;

public class DirCacheIndex implements Index {

	private DirCache dirCache;

	public DirCacheIndex(DirCache dirCache) {
		this.dirCache = dirCache;
	}

	@Override
	public Collection<IndexEntry> getEntries() {
		Collection<IndexEntry> emptyList = Collections.emptyList();
		return new ArrayList<IndexEntry>(emptyList);
	}

	@Override
	public Iterator<IndexEntry> iterator() {
		return getEntries().iterator();
	}

	@Override
	public IndexRewrite getIndexRewrite() {
		// TODO Auto-generated method stub
		return null;
	}

}
