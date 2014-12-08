package com.link_intersystems.tools.git.domain;

import java.util.Collection;
import java.util.Iterator;

public interface Index {

	Collection<IndexEntry> getEntries();

	Iterator<IndexEntry> iterator();

	IndexRewrite getIndexRewrite();

}
