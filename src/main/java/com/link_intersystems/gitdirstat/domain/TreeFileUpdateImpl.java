package com.link_intersystems.gitdirstat.domain;

import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;

public class TreeFileUpdateImpl implements TreeFileUpdate {

	private byte[] rawPath;
	private String pathString;
	private FileMode entryFileMode;
	private ObjectId entryObjectId;

	public TreeFileUpdateImpl(FileMode entryFileMode, ObjectId entryObjectId,
			byte[] rawPath, String pathString) {
		this.entryFileMode = entryFileMode;
		this.entryObjectId = entryObjectId;
		this.rawPath = rawPath;
		this.pathString = pathString;
	}

	/* (non-Javadoc)
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#delete()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.link_intersystems.tools.git.domain.TreeFileUpdate#getPath()
	 */
	@Override
	public String getPath() {
		return pathString;
	}

}
