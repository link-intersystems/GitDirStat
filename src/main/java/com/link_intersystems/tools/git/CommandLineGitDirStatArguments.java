package com.link_intersystems.tools.git;

import java.io.File;
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

	private static Option GIT_DIR_OPTION;

	static {
		OPTIONS = new Options();

		GIT_DIR_OPTION = new Option("git_dir", true,
				"The git repository directory. " + "If not specified the "
						+ "current work dir is considered the git repository.");

		OPTIONS.addOption(GIT_DIR_OPTION);
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
			formatter.printHelp("java " + GitDirStat.class.getName(), OPTIONS);
			throw new GitDirStatArgumentsParseException(e,
					SerializationUtils.clone(OPTIONS));
		}
	}

	public CommandLineGitDirStatArguments(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	public File getGitRepositoryDir() {
		String gitDirPathname = commandLine.getOptionValue(GIT_DIR_OPTION
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

	public boolean isUIEnabled() {
		// TODO GUI is implemented in a later version
		return false;
	}

}