package com.link_intersystems.junit.jgit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

public class RevstrCommitSelection implements CommitSelection {

	private Collection<String> revStrings;

	public RevstrCommitSelection(String... revstrs) {
		revStrings = Arrays.asList(revstrs);
	}

	public RevstrCommitSelection(List<String> revstrs) {
		revStrings = new HashSet<String>(revstrs);
	}

	@Override
	public boolean accept(RevCommit cmit) {
		ObjectId id = cmit.getId();
		AbbreviatedObjectId abbreviatedId = id.abbreviate(7);
		return revStrings.contains(abbreviatedId.name())
				|| revStrings.contains(id.name());
	}

}
