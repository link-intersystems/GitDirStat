package com.link_intersystems.tools.git.ui;

import java.io.File;

import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.link_intersystems.swing.AsyncActionMediator;
import com.link_intersystems.swing.CompositeAction;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.tools.git.GitDirStatApplication;
import com.link_intersystems.tools.git.GitDirStatArguments;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;

public class GUIApplication implements GitDirStatApplication {

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {
		setLookAndFeel();
		GitRepositoryModel repoModel = new GitRepositoryModel();
		File gitRepositoryDir = gitDirStatArguments.getGitRepositoryDir();
		if (gitRepositoryDir != null) {
			repoModel.setGitDir(gitRepositoryDir);
		}
		GitRepositoryAccess repoAccess = new GitRepositoryAccess();

		MainFrame mainFrame = new MainFrame(repoModel);

		SizeMetricsView sizeMetricsView = new SizeMetricsView();
		mainFrame.setMainComponent(sizeMetricsView);
		sizeMetricsView.setModel(repoModel);

		ProgressMonitor progressMonitor = mainFrame.getProgressMonitor();

		UpdateRefsAction updateRefsAction = new UpdateRefsAction(repoAccess,
				repoModel);
		UpdateTreeObjectAction updateAction = new UpdateTreeObjectAction(
				repoAccess, repoModel, progressMonitor);
		LoadRepositoryAction loadRepositoryAction = new LoadRepositoryAction(
				"Update Repository", updateRefsAction, updateAction);

		OpenAction openAction = new OpenAction(repoModel, loadRepositoryAction);

		mainFrame.addMenuBarAction(MainFrame.MB_PATH_FILE, openAction);
		mainFrame
				.addMenuBarAction(MainFrame.MB_PATH_FILE, loadRepositoryAction);
		Action applyBranchSelectionAction = sizeMetricsView
				.createApplyBranchSelectionAction();
		applyBranchSelectionAction.putValue(Action.NAME, "Apply selection");
		CompositeAction compositeAction = new CompositeAction(applyBranchSelectionAction,
				updateRefsAction, updateAction);
		AsyncActionMediator asyncActionMediator = new AsyncActionMediator(updateAction);
		asyncActionMediator.addDisabledActionWhileRunning(applyBranchSelectionAction);
		mainFrame.addToolbarAction(compositeAction);

		Action showTableAction = sizeMetricsView.getSetTableAction();
		showTableAction.putValue(Action.NAME, "Show table");
		Action showTreeAction = sizeMetricsView.getSetTreeAction();
		showTreeAction.putValue(Action.NAME, "Show tree");

		mainFrame.addMenuBarActionGroup(MainFrame.MENU_PATH_VIEW,
				showTableAction, showTreeAction);

		mainFrame.setVisible(true);

		if (gitRepositoryDir != null) {
			loadRepositoryAction.actionPerformed(null);
		}
	}

	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
	}

}
