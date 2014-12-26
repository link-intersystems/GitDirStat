package com.link_intersystems.gitdirstat.ui;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.Action;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.ui.UIContext.IconType;
import com.link_intersystems.io.FileUtils;
import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.ProgressMonitor;

public class UpdateRepositoryAction extends
		AsyncProgressAction<List<? extends Ref>, Void, TreeObject> {

	private static final long serialVersionUID = -6024006313949630749L;
	private GitRepositoryModel gitRepositoryModel;
	private GitRepositoryAccess gitRepositoryAccess;

	public UpdateRepositoryAction(UIContext uiContext,
			GitRepositoryModel gitRepositoryModel,
			GitRepositoryAccess gitRepositoryAccess) {
		this.gitRepositoryModel = gitRepositoryModel;
		this.gitRepositoryAccess = gitRepositoryAccess;
		putValue(Action.SMALL_ICON, uiContext.getIcon(IconType.UPDATE));
		putValue(Action.SHORT_DESCRIPTION, "Update repository");
		UpdateRepositoryActionInput updateRepositoryDialog = new UpdateRepositoryActionInput(
				gitRepositoryModel, uiContext, gitRepositoryAccess);
		setActionInputSource(updateRepositoryDialog);
		setProgressMonitor(uiContext.getProgressMonitor());
	}

	@Override
	protected TreeObject doInBackground(List<? extends Ref> refs,
			ProgressMonitor progressMonitor) throws Exception {
		GitRepository gitRepository = null;

		File gitDir = gitRepositoryModel.getGitDir();
		gitRepository = gitRepositoryAccess.getGitRepository(gitDir);

		List<? extends Ref> selectedRefs = refs;

		String progressMessage = MessageFormat.format(
				"Analyzing repository: {0}",
				FileUtils.abbreviatedPath(gitDir, 50));
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressMonitor, progressMessage);
		progressListenerMonitorAdapter.setUpdateInterval(250);
		TreeObject commitRangeTree = gitRepository.getCommitRangeTree(
				selectedRefs, progressListenerMonitorAdapter);
		return commitRangeTree;
	}

	@Override
	protected void done(TreeObject result) {
		gitRepositoryModel.setCommitRangeTree(result);
	}

}