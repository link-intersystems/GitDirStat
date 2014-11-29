package com.link_intersystems.tools.git.ui;

import java.io.File;

import javax.swing.Action;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.common.ProgressMonitor;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.ui.metrics.SizeMetricsTableComponent;
import com.link_intersystems.tools.git.ui.metrics.SizeMetricsTreeComponent;

public class GUIApplication implements GitDirStatApplication {

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {

		GitRepositoryModel repoModel = new GitRepositoryModel();
		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			repoModel.setGitDir(gitRepositoryDir);
		}
		GitRepositoryAccess repoAccess = new GitRepositoryAccess();

		MainFrame mainFrame = new MainFrame();

		SizeMetricsTableComponent sizeMetricsTableComponent = new SizeMetricsTableComponent();
		sizeMetricsTableComponent.setModel(repoModel);

		SizeMetricsTreeComponent sizeMetricsTreeComponent = new SizeMetricsTreeComponent();
		sizeMetricsTreeComponent.setModel(repoModel);

		mainFrame.setMainComponent(sizeMetricsTableComponent);

		ProgressMonitor progressMonitor = mainFrame.getProgressMonitor();

		UpdateAction updateAction = new UpdateAction(repoAccess, repoModel,
				progressMonitor);
		OpenAction openAction = new OpenAction(repoModel, updateAction);

		mainFrame.addMenuBarAction(MainFrame.MB_PATH_FILE, openAction);
		mainFrame.addMenuBarAction(MainFrame.MB_PATH_FILE, updateAction);

		Action showTableAction = mainFrame.createMainComponentSetterAction(
				"Table view", sizeMetricsTableComponent);
		Action showTreeAction = mainFrame.createMainComponentSetterAction(
				"Tree view", sizeMetricsTreeComponent);

		mainFrame.addMenuBarActionGroup(MainFrame.MB_PATH_VIEW,
				showTableAction, showTreeAction);

		mainFrame.setVisible(true);

		if (gitRepositoryDir != null) {
			updateAction.actionPerformed(null);
		}
	}

}
