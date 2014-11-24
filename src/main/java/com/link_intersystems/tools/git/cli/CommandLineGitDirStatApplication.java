package com.link_intersystems.tools.git.cli;

import java.io.OutputStream;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.service.GitMetricsService;
import com.link_intersystems.tools.git.service.NullProgressListener;
import com.link_intersystems.tools.git.service.SizeMetrics;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		GitRepository gitRepo = new GitRepository(arguments);
		GitMetricsService gitMetricsService = new GitMetricsService(gitRepo);
		CommitRange commitRange = gitRepo.getCommitRange(Constants.HEAD);
		SizeMetrics sizeMetrics = gitMetricsService.getSizeMetrics(commitRange,
				NullProgressListener.INSTANCE);

		SizesMetricsFormatter sizesMetricsFormatter = new SizesMetricsFormatter(
				sizeMetrics);

		OutputStream outputStream = arguments.getOutputStream();
		sizesMetricsFormatter.format(outputStream);
	}
}
