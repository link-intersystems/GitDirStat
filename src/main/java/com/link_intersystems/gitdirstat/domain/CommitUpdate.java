package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

import org.eclipse.jgit.lib.PersonIdent;

public interface CommitUpdate {

	public abstract String getMessage();

	public abstract String getOriginalMessage();

	public abstract PersonIdent getAuthor();

	public abstract void setAuthor(String name, String email);

	public abstract PersonIdent getCommitter();

	public abstract void setCommitter(String name, String email);

	public abstract TreeUpdate getTreeUpdate() throws IOException;

	public abstract String getId();

}