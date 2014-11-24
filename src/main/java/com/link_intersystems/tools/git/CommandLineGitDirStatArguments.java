package com.link_intersystems.tools.git;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

public class CommandLineGitDirStatArguments implements GitDirStatArguments {

	private static final String WORKING_DIR_SYS_PROP = "user.dir";

	private CommandLine commandLine;

	static Options OPTIONS;

	private static Option OPTION_GITDIR;
	private static Option OPTION_OUTFILE;

	static {
		OPTIONS = new Options();

		OPTION_GITDIR = new Option("gitdir", true,
				"The git repository directory. " + "If not specified the "
						+ "current work dir is considered the git repository.");

		OPTION_OUTFILE = new Option(
				"outfile",
				true,
				"The file where the output should be written to. "
						+ "If not specified or - is specified the stdout will be used.");

		OPTIONS.addOption(OPTION_GITDIR);
	}

	public static GitDirStatArguments parse(String[] args)
			throws GitDirStatArgumentsParseException {
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine commandLine = parser.parse(OPTIONS, args);
			GitDirStatArguments gitDirStatArguments = new CommandLineGitDirStatArguments(
					commandLine);
			return gitDirStatArguments;
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java " + GitDirStatCLI.class.getName(),
					OPTIONS);
			throw new GitDirStatArgumentsParseException(e,
					SerializationUtils.clone(OPTIONS));
		}
	}

	public CommandLineGitDirStatArguments(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public File getGitRepositoryDir() {
		String gitDirPathname = commandLine.getOptionValue(OPTION_GITDIR
				.getOpt());

		if (StringUtils.isBlank(gitDirPathname)) {
			gitDirPathname = System.getProperty(WORKING_DIR_SYS_PROP);
		}

		File gitDir = new File(gitDirPathname);
		if (!gitDir.isDirectory()) {
			String message = MessageFormat.format(
					"{0} does not seem to be a directory", gitDir);
			throw new IllegalArgumentException(message);
		}
		return gitDir;
	}

	@Override
	public OutputStream getOutputStream() {
		OutputStream outputStream = null;

		String outputOpt = OPTION_OUTFILE.getOpt();
		String outPathname = commandLine.getOptionValue(outputOpt);
		boolean useStdout = isBlank(outPathname) || "-".equals(outPathname);
		if (useStdout) {
			outputStream = System.out;
		} else {
			try {
				outputStream = new FileOutputStream(outPathname);
			} catch (FileNotFoundException e) {
				String message = MessageFormat.format(
						"{0} does not seem to be a a valid output file",
						outPathname);
				throw new IllegalArgumentException(message, e);
			}
		}
		return outputStream;
	}

}