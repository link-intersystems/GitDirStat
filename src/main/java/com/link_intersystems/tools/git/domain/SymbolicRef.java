package com.link_intersystems.tools.git.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.internal.storage.file.ReflogWriter;

public class SymbolicRef extends Ref {

	public SymbolicRef(GitRepository gitRepository,
			org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
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
