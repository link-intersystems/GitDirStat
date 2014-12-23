package com.link_intersystems.gitdirstat.cli;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.link_intersystems.gitdirstat.GitDirStatArgumentsParseException;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;
import com.link_intersystems.util.SortOrder;

public class GitDirStatListFilesArguments {

	private static final String WORKING_DIR_SYS_PROP = "user.dir";

	private CommandLine commandLine;

	static Options OPTIONS;

	private static Option OPTION_OUTFILE;
	private static Option OPTION_SORT_ORDER;
	private static Option OPTION_SORT_BY;

	static {
		OPTIONS = new Options();

		OPTION_OUTFILE = new Option(
				"outfile",
				true,
				"The file where the output should be written to. "
						+ "If not specified or - is specified the stdout will be used.");

		OPTION_SORT_ORDER = new Option("so", "sort_order", true,
				"The output's sort order. Either asc or desc. "
						+ " Output will be sorted by object sizes");

		OPTION_SORT_BY = new Option("sb", "sort_by", true,
				"The property by which the output should be sorted. Either size or name. "
						+ "Default is size.");

		OPTIONS.addOption(OPTION_OUTFILE);
		OPTIONS.addOption(OPTION_SORT_ORDER);
		OPTIONS.addOption(OPTION_SORT_BY);
	}

	public static GitDirStatListFilesArguments parse(String[] args)
			throws GitDirStatArgumentsParseException {
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine commandLine = parser.parse(OPTIONS, args);
			GitDirStatListFilesArguments gitDirStatArguments = new GitDirStatListFilesArguments(
					commandLine);
			File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
			if (gitRepositoryDir == null) {
				throw new ParseException(
						"A git repository directory must be provided");
			}
			return gitDirStatArguments;
		} catch (ParseException e) {
			throw new GitDirStatArgumentsParseException(e, "java "
					+ GitDirStatListFilesApplication.class.getName()
					+ " [GIT_REPOSITORY_DIR]",
					SerializationUtils.clone(OPTIONS));
		}
	}

	public GitDirStatListFilesArguments(CommandLine commandLine) {
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

	public SortOrder getSortOrder() {
		String sortOrderOption = commandLine.getOptionValue(OPTION_SORT_ORDER
				.getOpt());

		SortOrder sortOrder = null;

		if (StringUtils.isBlank(sortOrderOption)) {
			sortOrder = SortOrder.DESC;
		} else {
			sortOrder = SortOrder.valueOf(sortOrderOption.toUpperCase());
		}

		return sortOrder;
	}

	public TreeObjectSortBy getSortBy() {
		String sortByOption = commandLine.getOptionValue(OPTION_SORT_BY
				.getOpt());

		TreeObjectSortBy sortOrder = null;

		if (StringUtils.isBlank(sortByOption)) {
			sortOrder = TreeObjectSortBy.SIZE;
		} else {
			sortOrder = TreeObjectSortBy.valueOf(sortByOption.toUpperCase());
		}

		return sortOrder;
	}

}
