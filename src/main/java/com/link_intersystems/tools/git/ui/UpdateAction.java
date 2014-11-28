package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.common.ProgressMonitor;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.TreeObject;

public class UpdateAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private GitRepositoryAccess gitRepositoryAccess;
	private GitRepositoryModel gitRepositoryModel;
	private SizeMetricsSwingWorker sizeMetricsSwingWorker;
	private ProgressMonitor progressListener;

	public UpdateAction(GitRepositoryAccess gitRepositoryAccess,
			GitRepositoryModel gitRepositoryModel,
			ProgressMonitor progressListener) {
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.gitRepositoryModel = gitRepositoryModel;
		this.progressListener = progressListener;
		putValue(Action.NAME, "Update");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		sizeMetricsSwingWorker = new SizeMetricsSwingWorker(
				gitRepositoryAccess, gitRepositoryModel, progressListener);
		sizeMetricsSwingWorker.execute();

	}

	private class SizeMetricsSwingWorker extends SwingWorker<TreeObject, Void> {

		private GitRepositoryAccess gitRepositoryAccess;
		private ProgressMonitor progressListener;
		private GitRepositoryModel gitRepositoryModel;

		public SizeMetricsSwingWorker(GitRepositoryAccess gitRepositoryAccess,
				GitRepositoryModel gitRepositoryModel,
				ProgressMonitor progressListener) {
			this.gitRepositoryAccess = gitRepositoryAccess;
			this.gitRepositoryModel = gitRepositoryModel;
			this.progressListener = progressListener;
		}

		@Override
		protected TreeObject doInBackground() throws Exception {
			GitRepository gitRepository = null;

			String repositoryId = gitRepositoryModel.getRepositoryId();
			if (repositoryId == null) {
				File gitDir = gitRepositoryModel.getGitDir();
				gitRepository = gitRepositoryAccess.getGitRepository(gitDir);
			} else {
				gitRepository = gitRepositoryAccess
						.getGitRepository(repositoryId);
			}

			CommitRange commitRange = gitRepository
					.getCommitRange(Constants.HEAD);
			TreeObject commitRangeTree = gitRepository.getCommitRangeTree(
					commitRange, progressListener);
			commitRangeTree.asPathMap();
			return commitRangeTree;
		}

		@Override
		protected void done() {
			try {
				TreeObject treeObject = get();
				gitRepositoryModel.setCommitRangeTree(treeObject);
			} catch (InterruptedException ignore) {
			} catch (ExecutionException executionException) {
				Throwable cause = executionException.getCause();
				String msg = String.format("Unexpected problem: %s",
						cause.toString());
				JOptionPane.showMessageDialog(null, msg, "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}
}
