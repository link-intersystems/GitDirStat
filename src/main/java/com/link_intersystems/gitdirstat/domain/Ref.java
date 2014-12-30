package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.internal.storage.file.ReflogWriter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.lib.ReflogReader;
import org.eclipse.jgit.lib.Repository;

public abstract class Ref {

	private org.eclipse.jgit.lib.Ref jgitRef;
	private GitRepository gitRepository;

	Ref(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		this.gitRepository = gitRepository;
		this.jgitRef = jgitRef;
	}

	org.eclipse.jgit.lib.Ref getJgitRef() {
		return jgitRef;
	}


	public ObjectId getCommitId() {
		return getObjectId();
	}

	private ObjectId getObjectId() {
		return jgitRef.getObjectId();
	}

	public String getName() {
		return jgitRef.getName();
	}

	public String getSimpleName() {
		String name = jgitRef.getName();
		String simpleName = StringUtils.substringAfterLast(name, "/");
		return simpleName;
	}

	public List<ReflogEntry> getReflogEntries() throws IOException {
		Repository repo = gitRepository.getRepository();
		ReflogReader reflogReader = repo.getReflogReader(getName());
		List<ReflogEntry> reverseEntries = reflogReader.getReverseEntries();
		return reverseEntries;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getObjectId() == null) ? 0 : getObjectId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ref other = (Ref) obj;
		if (getObjectId() == null) {
			if (other.getObjectId() != null)
				return false;
		} else if (!getObjectId().equals(other.getObjectId()))
			return false;
		return true;
	}

	public void update(ObjectId newId) throws IOException {
		String refName = getName();
		Repository repository = gitRepository.getRepository();
		RefUpdate ru = repository.updateRef(refName);
		ru.setNewObjectId(newId);
		ru.setForceUpdate(true);
		ru.update();
	}

	public boolean isUpdateable() {
		return true;
	}

	public abstract void clearReflog() throws IOException;

	protected ReflogWriter getReflogWriter() {
		Repository repository = gitRepository.getRepository();
		ReflogWriter reflogWriter = new ReflogWriter(repository);
		return reflogWriter;
	}

	public void addReflogEntries(List<ReflogEntry> reflogEntries)
			throws IOException {
		String refName = getName();
		ReflogWriter reflogWriter = getReflogWriter();
		for (ReflogEntry nonExpiredEntry : reflogEntries) {
			ObjectId oldId = nonExpiredEntry.getOldId();
			ObjectId newId = nonExpiredEntry.getNewId();
			PersonIdent ident = nonExpiredEntry.getWho();
			String message = nonExpiredEntry.getComment();
			reflogWriter.log(refName, oldId, newId, ident, message);
		}

	}

	protected GitRepository getGitRepository() {
		return gitRepository;
	}

}
