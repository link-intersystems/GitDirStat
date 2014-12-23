package com.link_intersystems.gitdirstat.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.internal.storage.file.ReflogWriter;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Repository;

public class LocalBranch extends Branch {

	LocalBranch(GitRepository gitRepository, org.eclipse.jgit.lib.Ref jgitRef) {
		super(gitRepository, jgitRef);
	}

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

	public boolean isUpToDate() throws IOException {
		GitRepository gitRepository = getGitRepository();
		Repository repo = gitRepository.getRepository();
		BranchTrackingStatus branchTrackingStatus = BranchTrackingStatus.of(
				repo, getName());
		return branchTrackingStatus.getAheadCount() == 0
				&& branchTrackingStatus.getBehindCount() == 0;
	}

}
