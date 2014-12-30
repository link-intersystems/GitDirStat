package com.link_intersystems.gitdirstat.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

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
	protected void processException(Throwable cause,
			ExecutionContext<IndexFilter> executionContext) {
		if (cause instanceof RewriteBranchExistsException) {
			RewriteBranchExistsException rewriteBranchExistsException = (RewriteBranchExistsException) cause;
			String branchname = rewriteBranchExistsException.getBranchName();
			String msg = String
					.format("Rewrite branch named %s already exists. Do you want to delete it now?",
							branchname);
			int result = JOptionPane.showConfirmDialog(null, msg,
					"Rewrite branch exists", JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				File gitDir = gitRepositoryModel.getGitDir();
				GitRepository gitRepository = gitRepositoryAccess
						.getGitRepository(gitDir);
				Git git = gitRepository.getGit();
				DeleteBranchCommand branchDelete = git.branchDelete();
				branchDelete.setBranchNames(branchname);
				branchDelete.setForce(true);
				try {
					branchDelete.call();
					execute(executionContext);
				} catch (GitAPIException e) {
					super.processException(e, executionContext);
				}
			}
		} else {
			super.processException(cause, executionContext);
		}
	}

	@Override
	protected Void doInBackground(IndexFilter actionInput,
			ProgressMonitor progressMonitor) throws Exception {
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressMonitor, "Removing paths from repository");
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
		openRepositoryAction.setProgressMonitor(progressMonitor);
		openRepositoryAction.actionPerformed(null);
	}
}
