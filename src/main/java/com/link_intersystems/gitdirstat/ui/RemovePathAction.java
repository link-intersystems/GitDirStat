package com.link_intersystems.gitdirstat.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.gitdirstat.domain.RewriteBranchExistsException;
import com.link_intersystems.swing.ActionInputSource;
import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.ProgressMonitor;

public class RemovePathAction extends
		AsyncProgressAction<IndexFilter, Void, Void> {

	private static final long serialVersionUID = -2409080673565317180L;
	private GitRepositoryModel gitRepositoryModel;
	private GitRepositoryAccess gitRepositoryAccess;
	private OpenRepositoryAction openRepositoryAction;
	private UIContext uiContext;

	public RemovePathAction(final GitRepositoryModel gitRepositoryModel,
			final GitRepositoryAccess gitRepositoryAccess, UIContext uiContext) {
		this.gitRepositoryModel = gitRepositoryModel;
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.uiContext = uiContext;
		setActionInputSource(new RemovePathsActionInput(gitRepositoryModel,
				uiContext, gitRepositoryAccess));
		openRepositoryAction = new OpenRepositoryAction(uiContext,
				gitRepositoryModel, gitRepositoryAccess);
		openRepositoryAction
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
		try {

			gitRepository.applyFilter(actionInput,
					progressListenerMonitorAdapter);
		} catch (RewriteBranchExistsException rewriteBranchExistsException) {

		}

		return null;
	}

	@Override
	protected void done(Void result) {
		ProgressMonitor progressMonitor = uiContext.getProgressMonitor();
		openRepositoryAction.setProgressMonitor(progressMonitor);
		openRepositoryAction.actionPerformed(null);
	}
}
