package com.link_intersystems.gitdirstat.domain;

public class RewriteBranchExistsException extends RuntimeException {

	private static final long serialVersionUID = 2142137189713898997L;
	private String branchname;

	public RewriteBranchExistsException(String message, String branchname) {
		super(message);
		this.branchname = branchname;
	}

	public String getBranchName() {
		return branchname;
	}

}
