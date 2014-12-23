package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import com.link_intersystems.swing.ActionInputSource;
import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.IndexFilter;
import com.link_intersystems.tools.git.domain.Ref;

public class RemovePathAction extends
		AsyncProgressAction<IndexFilter, Void, Void> {

	private static final long serialVersionUID = -2409080673565317180L;
	private GitRepositoryModel gitRepositoryModel;
	private GitRepositoryAccess gitRepositoryAccess;
	private UpdateRepositoryAction updateRepositoryAction;
	private UIContext uiContext;

	public RemovePathAction(final GitRepositoryModel gitRepositoryModel,
			final GitRepositoryAccess gitRepositoryAccess, UIContext uiContext) {
		this.gitRepositoryModel = gitRepositoryModel;
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.uiContext = uiContext;
		setActionInputSource(new RemovePathsActionInput(gitRepositoryModel,
				uiContext, gitRepositoryAccess));
		updateRepositoryAction = new UpdateRepositoryAction(uiContext,
				gitRepositoryModel, gitRepositoryAccess);
		updateRepositoryAction
				.setActionInputSource(new ActionInputSource<List<? extends Ref>>() {

					@Override
					public List<? extends Ref> getActionInput(ActionEvent e) {
						File gitDir = gitRepositoryModel.getGitDir();
						GitRepository gitRepository = gitRepositoryAccess
								.getGitRepository(gitDir);
						return gitRepository.getRefs(Ref.class);
					}
				});
	}

	@Override
	protected Void doInBackground(IndexFilter actionInput,
			ProgressMonitor progressMonitor) throws Exception {
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressMonitor, "Removing paths from history");
		progressListenerMonitorAdapter.setUpdateInterval(1000);

		File gitDir = gitRepositoryModel.getGitDir();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitDir);
		gitRepository.applyFilter(actionInput, progressListenerMonitorAdapter);

		return null;
	}

	@Override
	protected void done(Void result) {
		ProgressMonitor progressMonitor = uiContext.getProgressMonitor();
		updateRepositoryAction.setProgressMonitor(progressMonitor);
		updateRepositoryAction.actionPerformed(null);
	}
}
