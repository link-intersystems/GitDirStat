package com.link_intersystems.tools.git.domain;

import org.apache.commons.lang3.StringUtils;

public class Ref {

	private org.eclipse.jgit.lib.Ref jgitRef;

	Ref(org.eclipse.jgit.lib.Ref jgitRef) {
		this.jgitRef = jgitRef;
	}

	org.eclipse.jgit.lib.Ref getJgitRef() {
		return jgitRef;
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

}
