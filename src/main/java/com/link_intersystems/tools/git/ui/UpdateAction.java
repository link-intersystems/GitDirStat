package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;

import org.eclipse.jgit.lib.Constants;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.service.GitMetricsService;
import com.link_intersystems.tools.git.service.ProgressListener;
import com.link_intersystems.tools.git.service.SizeMetrics;

public class UpdateAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private SizeMetricsTableModel sizeMetricsTableModel;
	private GitMetricsService gitMetricsService;
	private GitRepository gitRepository;
	private SizeMetricsSwingWorker sizeMetricsSwingWorker;
	private ProgressListener progressListener;

	public UpdateAction(SizeMetricsTableModel sizeMetricsTableModel,
			GitMetricsService gitMetricsService, GitRepository gitRepository,
			ProgressListener progressListener) {
		this.sizeMetricsTableModel = sizeMetricsTableModel;
		this.gitMetricsService = gitMetricsService;
		this.gitRepository = gitRepository;
		this.progressListener = progressListener;
		putValue(Action.NAME, "Update");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		sizeMetricsSwingWorker = new SizeMetricsSwingWorker(gitMetricsService,
				gitRepository, sizeMetricsTableModel, progressListener);
		sizeMetricsSwingWorker.execute();

	}

	private class SizeMetricsSwingWorker extends SwingWorker<SizeMetrics, Void> {

		private GitMetricsService gitMetricsService;
		private GitRepository gitRepository;
		private SizeMetricsTableModel sizeMetricsTableModel;
		private ProgressListener progressListener;

		public SizeMetricsSwingWorker(GitMetricsService gitMetricsService,
				GitRepository gitRepository,
				SizeMetricsTableModel sizeMetricsTableModel,
				ProgressListener progressListener) {
			this.gitMetricsService = gitMetricsService;
			this.gitRepository = gitRepository;
			this.sizeMetricsTableModel = sizeMetricsTableModel;
			this.progressListener = progressListener;
		}

		@Override
		protected SizeMetrics doInBackground() throws Exception {
			CommitRange commitRange = gitRepository
					.getCommitRange(Constants.HEAD);
			SizeMetrics sizeMetrics = gitMetricsService.getSizeMetrics(
					commitRange, progressListener);
			return sizeMetrics;
		}

		@Override
		protected void done() {
			try {
				SizeMetrics sizeMetrics = get();
				sizeMetricsTableModel.setSizeMetrics(sizeMetrics);
			} catch (InterruptedException ignore) {
			} catch (ExecutionException ignore) {
			}
		}

	}
}
