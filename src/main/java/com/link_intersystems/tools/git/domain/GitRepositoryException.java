package com.link_intersystems.tools.git.domain;

public class GitRepositoryException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 4904716422580821815L;

	public GitRepositoryException(Throwable cause) {
		super(cause);
	}

	public GitRepositoryException(String message) {
		super(message);
	}

}
