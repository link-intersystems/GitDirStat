package com.link_intersystems.tools.git.cli;

import java.io.File;
import java.io.OutputStream;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.service.GetSizeMetricsRequest;
import com.link_intersystems.tools.git.service.GitRepositoryService;
import com.link_intersystems.tools.git.service.NullProgressListener;
import com.link_intersystems.tools.git.service.SizeMetrics;

public class CommandLineGitDirStatApplication implements GitDirStatApplication {

	public void run(GitDirStatArguments arguments) throws Exception {
		File gitRepositoryDir = arguments.getGitRepositoryDir();
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepositoryService gitMetricsService = new GitRepositoryService(
				gitRepositoryAccess);

		String repositoryId = gitMetricsService.newRepository(gitRepositoryDir);

		GetSizeMetricsRequest getSizeMetricsRequest = new GetSizeMetricsRequest(
				repositoryId, Constants.HEAD, NullProgressListener.INSTANCE);
		SizeMetrics sizeMetrics = gitMetricsService
				.getSizeMetrics(getSizeMetricsRequest);

		SizesMetricsFormatter sizesMetricsFormatter = new SizesMetricsFormatter(
				sizeMetrics);

		OutputStream outputStream = arguments.getOutputStream();
		sizesMetricsFormatter.format(outputStream);
	}
}
