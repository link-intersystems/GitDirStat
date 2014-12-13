package com.link_intersystems.tools.git.domain;

import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;

public class TreeFileUpdate {

	private byte[] rawPath;
	private String pathString;
	private FileMode entryFileMode;
	private ObjectId entryObjectId;

	public TreeFileUpdate(FileMode entryFileMode, ObjectId entryObjectId,
			byte[] rawPath, String pathString) {
		this.entryFileMode = entryFileMode;
		this.entryObjectId = entryObjectId;
		this.rawPath = rawPath;
		this.pathString = pathString;
	}

	public void delete() {
		this.rawPath = null;
	}

	void apply(DirCacheBuilder builder) {
		if (rawPath != null) {
			DirCacheEntry entry = new DirCacheEntry(rawPath);
			entry.setFileMode(entryFileMode);
			entry.setObjectId(entryObjectId);
			builder.add(entry);
		}
	}

	public String getPath() {
		return pathString;
	}

}
