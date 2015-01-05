package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;

public interface TreeEntryWalk {

	public interface TreeEntry {

		FileMode getFileMode();

		String getPathString();

		ObjectId getObjectId();

		long getSize() throws IOException;

		byte[] getRawPath();

		TreeEntryEquality getEqualityObject();

		public interface TreeEntryEquality {

		}
	}



	void walk(TreeEntry treeEntry) throws IOException;

}
