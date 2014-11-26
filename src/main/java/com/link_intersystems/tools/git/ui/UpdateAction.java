package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.service.GetSizeMetricsRequest;
import com.link_intersystems.tools.git.service.GitRepositoryService;
import com.link_intersystems.tools.git.service.ProgressListener;
import com.link_intersystems.tools.git.service.SizeMetrics;

public class UpdateAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private SizeMetricsTableModel sizeMetricsTableModel;
	private GitRepositoryService gitRepositoryService;
	private GitRepositoryModel gitRepositoryModel;
	private SizeMetricsSwingWorker sizeMetricsSwingWorker;
	private ProgressListener progressListener;
	private SizeMetricsTreeModel sizeMetricsTreeModel;

	public UpdateAction(SizeMetricsTableModel sizeMetricsTableModel,
			GitRepositoryService gitRepositoryService,
			GitRepositoryModel gitRepositoryModel,
			ProgressListener progressListener, SizeMetricsTreeModel sizeMetricsTreeModel) {
		this.sizeMetricsTableModel = sizeMetricsTableModel;
		this.gitRepositoryService = gitRepositoryService;
		this.gitRepositoryModel = gitRepositoryModel;
		this.progressListener = progressListener;
		this.sizeMetricsTreeModel = sizeMetricsTreeModel;
		putValue(Action.NAME, "Update");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		sizeMetricsSwingWorker = new SizeMetricsSwingWorker(
				gitRepositoryService, gitRepositoryModel,
				sizeMetricsTableModel, progressListener);
		sizeMetricsSwingWorker.execute();

	}

	private class SizeMetricsSwingWorker extends SwingWorker<SizeMetrics, Void> {

		private GitRepositoryService gitRepositoryService;
		private SizeMetricsTableModel sizeMetricsTableModel;
		private ProgressListener progressListener;
		private GitRepositoryModel gitRepositoryModel;

		public SizeMetricsSwingWorker(
				GitRepositoryService gitRepositoryService,
				GitRepositoryModel gitRepositoryModel,
				SizeMetricsTableModel sizeMetricsTableModel,
				ProgressListener progressListener) {
			this.gitRepositoryService = gitRepositoryService;
			this.gitRepositoryModel = gitRepositoryModel;
			this.sizeMetricsTableModel = sizeMetricsTableModel;
			this.progressListener = progressListener;
		}

		@Override
		protected SizeMetrics doInBackground() throws Exception {
			String repositoryId = gitRepositoryModel.getRepositoryId();
			if (repositoryId == null) {
				File gitDir = gitRepositoryModel.getGitDir();
				repositoryId = gitRepositoryService.newRepository(gitDir);
			}
			GetSizeMetricsRequest getSizeMetricsRequest = new GetSizeMetricsRequest(
					repositoryId, Constants.HEAD, progressListener);
			SizeMetrics sizeMetrics = gitRepositoryService
					.getSizeMetrics(getSizeMetricsRequest);
			return sizeMetrics;
		}

		@Override
		protected void done() {
			try {
				SizeMetrics sizeMetrics = get();
				sizeMetricsTableModel.setSizeMetrics(sizeMetrics);
				sizeMetricsTreeModel.setSizeMetrics(sizeMetrics);
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
