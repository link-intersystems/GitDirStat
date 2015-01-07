package com.link_intersystems.gitdirstat.ui;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.gitdirstat.GitDirStatArgumentsParseException;

public class CommandLineGitDirStatUIArguments implements GitDirStatUIArguments {

	private static final String WORKING_DIR_SYS_PROP = "user.dir";

	private CommandLine commandLine;

	static Options OPTIONS;


	static {
		OPTIONS = new Options();
	}

	public static CommandLineGitDirStatUIArguments parse(String[] args)
			throws GitDirStatArgumentsParseException {
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine commandLine = parser.parse(OPTIONS, args);
			CommandLineGitDirStatUIArguments gitDirStatArguments = new CommandLineGitDirStatUIArguments(
					commandLine);
			return gitDirStatArguments;
		} catch (ParseException e) {
			throw new GitDirStatArgumentsParseException(e, "java "
					+ GitDirStatUI.class.getName() + " [GIT_REPOSITORY_DIR]",
					SerializationUtils.clone(OPTIONS));
		}
	}

	public CommandLineGitDirStatUIArguments(CommandLine commandLine) {
		this.commandLine = commandLine;
	}

	@SuppressWarnings("unchecked")
	public File getGitRepositoryDir() {
		List<String> argList = commandLine.getArgList();
		if (argList.isEmpty()) {
			return null;
		}
		String gitDirPathname = argList.get(0);

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



}
