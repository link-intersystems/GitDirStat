package com.link_intersystems.gitdirstat.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.internal.storage.file.ReflogWriter;
import org.eclipse.jgit.lib.ObjectId;

public class NamedRef extends Ref {

	public NamedRef(GitRepository gitRepository,
			org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

	@Override
	public void update(ObjectId newId) throws IOException {
		org.eclipse.jgit.lib.Ref jgitRef = getJgitRef();
		if (jgitRef.isSymbolic()) {
			// TODO should we delegate to the ref this ref points to?
		} else {
			super.update(newId);
		}
	}

	@Override
	public void clearReflog() throws IOException {
		String refName = getName();
		ReflogWriter reflogWriter = getReflogWriter();
		File logFor = reflogWriter.logFor(refName);
		FileOutputStream fout = new FileOutputStream(logFor);
		try {
			fout.write(new byte[0]);
		} finally {
			IOUtils.closeQuietly(fout);
		}
	}

}
