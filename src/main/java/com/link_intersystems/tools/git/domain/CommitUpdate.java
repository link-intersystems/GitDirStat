package com.link_intersystems.tools.git.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

public interface CommitUpdate {

	public abstract TreeUpdate getTreeUpdate(TreeFilter treeFilter)
			throws IOException;

	public abstract String getMessage();

	public abstract String getOriginalMessage();

	public abstract PersonIdent getAuthor();

	public abstract void setAuthor(String name, String email);

	public abstract PersonIdent getCommitter();

	public abstract void setCommitter(String name, String email);

	public abstract TreeUpdate getTreeUpdate() throws IOException;

	public abstract String getId();

}