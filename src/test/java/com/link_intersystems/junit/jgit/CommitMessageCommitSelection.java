package com.link_intersystems.junit.jgit;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class CommitMessageCommitSelection implements CommitSelection {

	public static final MatchMode CONTAINS = new MatchMode() {

		public boolean matches(String s1, String s2) {
			return s1.contains(s2);
		}
	};

	public static final MatchMode EQUALS = new MatchMode() {

		public boolean matches(String s1, String s2) {
			return s1.equals(s2);
		}
	};

	public static abstract class MatchMode {

		private MatchMode() {
		}

		public abstract boolean matches(String s1, String s2);
	}

	private Collection<String> messages;
	private MatchMode matchMode = CONTAINS;

	public CommitMessageCommitSelection(String... messages) {
		this.messages = Arrays.asList(messages);
	}

	public CommitMessageCommitSelection(List<String> messages) {
		this.messages = new HashSet<String>(messages);
	}

	public void setMatchMode(MatchMode matchMode) {
		if (matchMode == null) {
			matchMode = CONTAINS;
		}
		this.matchMode = matchMode;
	}

	@Override
	public boolean accept(RevCommit cmit) {
		String fullMessage = cmit.getFullMessage();
		for (String message : messages) {
			if (matchMode.matches(fullMessage, message)) {
				return true;
			}
		}
		return false;
	}

}
