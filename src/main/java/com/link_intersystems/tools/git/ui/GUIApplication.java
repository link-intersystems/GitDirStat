package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.service.GitMetricsService;
import com.link_intersystems.tools.git.service.ProgressListener;

public class GUIApplication implements GitDirStatApplication {

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {
		JFrame mainFrame = new JFrame("GitDirStat");
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setSize(1024, 768);
		mainFrame.setLocationRelativeTo(null);

		Container contentPane = mainFrame.getContentPane();

		contentPane.setLayout(new BorderLayout());

		SizeMetricsTableModel dm = new SizeMetricsTableModel();
		JTable resultTable = new JTable(dm);

		JScrollPane resultTableScrollPane = new JScrollPane(resultTable);
		contentPane.add(resultTableScrollPane, BorderLayout.CENTER);

		JProgressBar jProgressBar = new JProgressBar();
		jProgressBar.setVisible(false);
		jProgressBar.setStringPainted(true);
		BoundedRangeModel progressModel = jProgressBar.getModel();
		ProgressListener progressListener = new BoundedRangeModelProgressListener(
				progressModel);
		ComponentVisibleOnProgress componentVisibleOnProgress = new ComponentVisibleOnProgress(
				progressListener, jProgressBar);
		contentPane.add(jProgressBar, BorderLayout.SOUTH);

		GitRepository gitRepo = new GitRepository(gitDirStatArguments);
		GitMetricsService gitMetricsService = new GitMetricsService(gitRepo);
		UpdateAction updateAction = new UpdateAction(dm, gitMetricsService,
				gitRepo, componentVisibleOnProgress);

		JToolBar toolbar = new JToolBar();
		toolbar.setRollover(true);
		toolbar.add(updateAction);
		contentPane.add(toolbar, BorderLayout.NORTH);

		mainFrame.setVisible(true);
	}

}
