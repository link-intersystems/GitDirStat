package com.link_intersystems.tools.git.ui;

import java.io.File;

import javax.swing.Action;

import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.service.GitRepositoryService;
import com.link_intersystems.tools.git.service.ProgressMonitor;

public class GUIApplication implements GitDirStatApplication {

	private GitRepositoryService gitRepositoryService;

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {

		GitRepositoryModel repoModel = new GitRepositoryModel();
		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			repoModel.setGitDir(gitRepositoryDir);
		}

		MainFrame mainFrame = new MainFrame();

		SizeMetricsTableComponent sizeMetricsTableComponent = new SizeMetricsTableComponent();
		sizeMetricsTableComponent.setModel(repoModel);

		SizeMetricsTreeComponent sizeMetricsTreeComponent = new SizeMetricsTreeComponent();
		sizeMetricsTreeComponent.setModel(repoModel);

		mainFrame.setMainComponent(sizeMetricsTableComponent);

		ProgressMonitor progressMonitor = mainFrame.getProgressMonitor();
		GitRepositoryService repoService = getGitRepositoryService();

		UpdateAction updateAction = new UpdateAction(repoService, repoModel,
				progressMonitor);
		OpenAction openAction = new OpenAction(repoModel, updateAction);

		mainFrame.addMenuBarAction(MainFrame.MB_PATH_FILE, openAction);
		mainFrame.addMenuBarAction(MainFrame.MB_PATH_FILE, updateAction);

		Action showTableAction = mainFrame.createMainComponentSetterAction(
				"Show Table", sizeMetricsTableComponent);
		Action showTreeAction = mainFrame.createMainComponentSetterAction(
				"Show Tree", sizeMetricsTreeComponent);

		mainFrame.addMenuBarActionGroup(MainFrame.MB_PATH_VIEW,
				showTableAction, showTreeAction);

		mainFrame.setVisible(true);

		if (gitRepositoryDir != null) {
			updateAction.actionPerformed(null);
		}
	}

	private GitRepositoryService getGitRepositoryService() {
		if (gitRepositoryService == null) {
			GitRepositoryAccess gitRepositoryAccess = new GitRepositoryAccess();
			gitRepositoryService = new GitRepositoryService(gitRepositoryAccess);
		}
		return gitRepositoryService;
	}

}
