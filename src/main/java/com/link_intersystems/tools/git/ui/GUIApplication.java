package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.math.BigInteger;

import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.service.GitRepositoryService;
import com.link_intersystems.tools.git.service.ProgressListener;

public class GUIApplication implements GitDirStatApplication {

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {
		GitRepositoryModel gitRepositoryModel = new GitRepositoryModel();

		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			gitRepositoryModel.setGitDir(gitRepositoryDir);
		}

		JFrame mainFrame = new JFrame("GitDirStat");
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setSize(1024, 768);
		mainFrame.setLocationRelativeTo(null);

		Container contentPane = mainFrame.getContentPane();

		contentPane.setLayout(new BorderLayout());

		SizeMetricsTableModel dm = new SizeMetricsTableModel();
		JTable resultTable = new JTable(dm);
		resultTable.setRowSorter(new TableRowSorter<TableModel>(dm));
		resultTable.setDefaultRenderer(BigInteger.class, new HumanReadableFileSizeTableCellRenderer());

		JScrollPane resultTableScrollPane = new JScrollPane(resultTable);
		contentPane.add(resultTableScrollPane, BorderLayout.CENTER);

		JProgressBar jProgressBar = new JProgressBar();
		jProgressBar.setVisible(true);
		jProgressBar.setStringPainted(true);
		BoundedRangeModel progressModel = jProgressBar.getModel();
		ProgressListener progressListener = new BoundedRangeModelProgressListener(
				progressModel);
		ComponentVisibleOnProgress componentVisibleOnProgress = new ComponentVisibleOnProgress(
				progressListener, jProgressBar);
		contentPane.add(jProgressBar, BorderLayout.SOUTH);
		GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
		GitRepositoryService gitRepositoryService = new GitRepositoryService(
				gitRepositoryAccess);

		UpdateAction updateAction = new UpdateAction(dm, gitRepositoryService,
				gitRepositoryModel, componentVisibleOnProgress);

		OpenAction openAction = new OpenAction(gitRepositoryModel, updateAction);

		JToolBar toolbar = new JToolBar();
		toolbar.setRollover(true);
		toolbar.add(openAction);
		toolbar.add(updateAction);
		contentPane.add(toolbar, BorderLayout.NORTH);

		mainFrame.setVisible(true);

		if (gitRepositoryDir != null) {
			updateAction.actionPerformed(null);
		}
	}

}
