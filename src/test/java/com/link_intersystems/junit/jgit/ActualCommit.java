package com.link_intersystems.junit.jgit;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

public class ActualCommit {

	private RevCommit revCommit;

	public ActualCommit(RevCommit revCommit) {
		this.revCommit = revCommit;
	}

	public ActualAuthor getAuthor() {
		PersonIdent authorIdent = revCommit.getAuthorIdent();
		ActualAuthor personAssertion = new ActualAuthor(authorIdent,
				revCommit);
		return personAssertion;
	}

	public ActualAuthor getCommitterAssertion() {
		PersonIdent committerIdent = revCommit.getCommitterIdent();
		ActualAuthor personAssertion = new ActualAuthor(committerIdent,
				revCommit);
		return personAssertion;
	}

}
