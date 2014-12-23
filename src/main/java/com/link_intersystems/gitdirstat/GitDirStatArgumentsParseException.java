package com.link_intersystems.gitdirstat;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GitDirStatArgumentsParseException extends ParseException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8608827448040505633L;
	private Options options;
	private String cmd;

	public GitDirStatArgumentsParseException(ParseException e, String cmd,
			Options options) {
		super(e.getLocalizedMessage());
		this.cmd = cmd;
		this.options = options;
		initCause(e);
	}

	public Options getOptions() {
		return options;
	}

	public void printHelp(OutputStream outputStream) {
		HelpFormatter formatter = new HelpFormatter();
		PrintWriter pw = new PrintWriter(outputStream);
		formatter.printHelp(pw, 100, cmd, "", getOptions(), 5, 5, "Error: "
				+ getLocalizedMessage());
		pw.flush();
		pw.close();
	}
}
