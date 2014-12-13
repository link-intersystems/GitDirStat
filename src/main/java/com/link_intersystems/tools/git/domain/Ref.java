package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;

public class Ref {

	private org.eclipse.jgit.lib.Ref jgitRef;
	private GitRepository gitRepository;

	Ref(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		this.gitRepository = gitRepository;
		this.jgitRef = jgitRef;
	}

	org.eclipse.jgit.lib.Ref getJgitRef() {
		return jgitRef;
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

}
