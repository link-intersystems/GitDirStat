package com.link_intersystems.gitdirstat.domain;

import java.util.Iterator;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;

public interface TreeUpdate extends Iterator<TreeFileUpdate> {

	void release();

	ObjectId apply(ObjectInserter objectInserter);

	boolean hasUpdates();

}