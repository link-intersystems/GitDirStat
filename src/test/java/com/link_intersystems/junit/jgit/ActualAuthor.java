package com.link_intersystems.junit.jgit;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;

public class ActualAuthor {

	private PersonIdent authorIdent;
	private RevCommit revCommit;

	ActualAuthor(PersonIdent authorIdent, RevCommit revCommit) {
		this.authorIdent = authorIdent;
		this.revCommit = revCommit;
	}

	public void assertEmailEquals(String expectedEmail) throws Exception {
		String email = authorIdent.getEmailAddress();
		Assert.assertEquals("Author email of commit " + getShortCommitId()
				+ " ", expectedEmail, email);
	}

	public void assertNameEquals(String expectedName) throws Exception {
		String name = authorIdent.getName();
		Assert.assertEquals(
				"Author name of commit " + getShortCommitId() + " ",
				expectedName, name);
	}

	private String getShortCommitId() {
		return revCommit.getId().abbreviate(7).name();
	}

}
