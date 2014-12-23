package com.link_intersystems.gitdirstat;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GitDirStatArgumentsParseException extends ParseException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8608827448040505633L;
	private Options options;

	public GitDirStatArgumentsParseException(ParseException e, Options options) {
		super(e.getLocalizedMessage());
		this.options = options;
		initCause(e);
	}

	public Options getOptions() {
		return options;
	}
}
