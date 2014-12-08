package com.link_intersystems.tools.git.ui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;

import com.link_intersystems.io.FileUtils;
import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.Ref;
import com.link_intersystems.tools.git.domain.TreeObject;

public class UpdateTreeObjectAction extends
		AsyncProgressAction<TreeObject, Void> {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private GitRepositoryAccess gitRepositoryAccess;
	private GitRepositoryModel gitRepositoryModel;
	private ProgressMonitor progressListener;

	public UpdateTreeObjectAction(GitRepositoryAccess gitRepositoryAccess,
			GitRepositoryModel gitRepositoryModel,
			ProgressMonitor progressListener) {
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.gitRepositoryModel = gitRepositoryModel;
		this.progressListener = progressListener;
		putValue(Action.NAME, "Update");
	}

	@Override
	protected TreeObject doInBackground() throws IOException {
		GitRepository gitRepository = null;

		File gitDir = gitRepositoryModel.getGitDir();
		gitRepository = gitRepositoryAccess.getGitRepository(gitDir);

		List<? extends Ref> selectedRefs = gitRepositoryModel.getSelectedRefs();

		String progressMessage = MessageFormat.format(
				"Loading repository: {0}",
				FileUtils.abbreviatedPath(gitDir, 50));
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressListener, progressMessage);
		TreeObject commitRangeTree = gitRepository.getCommitRangeTree(
				selectedRefs, progressListenerMonitorAdapter);
		return commitRangeTree;
	}

	@Override
	protected void done(TreeObject treeObject) {
		gitRepositoryModel.setCommitRangeTree(treeObject);
	}
}
