package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;

public class TreeWalkerEntry {

	private ObjectId objectId;
	private String pathString;
	private GitRepository gitRepository;
	private int hashCode;
	private ObjectSize objectSize;
	private byte[] uniqueRaw;

	TreeWalkerEntry(byte[] rawPath, String pathString, ObjectId objectId,
			GitRepository gitRepository) {
		this.pathString = pathString;
		this.objectId = objectId;
		this.gitRepository = gitRepository;
		uniqueRaw = new byte[Constants.OBJECT_ID_LENGTH + rawPath.length];
		objectId.copyRawTo(uniqueRaw, 0);
		System.arraycopy(rawPath, 0, uniqueRaw, Constants.OBJECT_ID_LENGTH,
				rawPath.length);
		this.hashCode = Arrays.hashCode(uniqueRaw);
	}

	public ObjectId getId() {
		return objectId;
	}

	public String getPathString() {
		return pathString;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeWalkerEntry other = (TreeWalkerEntry) obj;
		if (Arrays.equals(uniqueRaw, other.uniqueRaw))
			return false;
		return true;
	}

	public ObjectSize getSize() throws IOException {
		if (this.objectSize == null) {
			ObjectReader objectReader = gitRepository.getObjectReader();
			long size = objectReader
					.getObjectSize(objectId, Constants.OBJ_BLOB);
			objectSize = new ObjectSize(objectId, size);
		}
		return objectSize;
	}

}
