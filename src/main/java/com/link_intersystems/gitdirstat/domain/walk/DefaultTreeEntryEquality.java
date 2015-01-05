package com.link_intersystems.gitdirstat.domain.walk;

import org.eclipse.jgit.lib.ObjectId;

import com.link_intersystems.gitdirstat.domain.TreeEntryWalk.TreeEntry.TreeEntryEquality;

class DefaultTreeEntryEquality implements TreeEntryEquality {

	private ObjectId objectId;
	private byte[] rawPath;

	public DefaultTreeEntryEquality(ObjectId objectId, byte[] rawPath) {
		this.objectId = objectId;
		this.rawPath = rawPath;
	}

	@Override
	public int hashCode() {
		return  objectId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultTreeEntryEquality other = (DefaultTreeEntryEquality) obj;

		if (this.hashCode() != other.hashCode()) {
			return false;
		}

		if (!objectId.equals(other.objectId))
			return false;
		if (!equals(rawPath, other.rawPath))
			return false;
		return true;
	}

	public static boolean equals(byte[] a, byte[] a2) {
		int length = a.length;
		if (a2.length != length)
			return false;

		for (int i = length - 1; i > -1; i--)
			if (a[i] != a2[i])
				return false;

		return true;
	}
}