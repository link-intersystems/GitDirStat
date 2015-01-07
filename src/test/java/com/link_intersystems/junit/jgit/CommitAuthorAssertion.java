package com.link_intersystems.junit.jgit;

public class CommitAuthorAssertion implements CommitAssertion {

	private String expectedAuthorName;
	private String expectedAuthorEmail;

	public CommitAuthorAssertion(String expectedAuthorName,
			String expectedAuthorEmail) {
		this.expectedAuthorName = expectedAuthorName;
		this.expectedAuthorEmail = expectedAuthorEmail;
	}

	@Override
	public void assertCommit(ActualCommit actualCommit) throws Exception {
		ActualAuthor actualAuthor = actualCommit.getAuthor();
		actualAuthor.assertNameEquals(expectedAuthorName);
		actualAuthor.assertEmailEquals(expectedAuthorEmail);
	}

}
