package com.link_intersystems.gitdirstat.ui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.ui.UIContext.IconType;

public class OpenAction extends AbstractAction {

	private static final long serialVersionUID = 6082672924263782869L;
	private GitRepositoryModel gitRepositoryModel;
	private OpenRepositoryAction openRepositoryAction;

	public OpenAction(GitRepositoryModel gitRepositoryModel,
			GitRepositoryAccess gitRepositoryAccess, UIContext uiContext) {
		this.gitRepositoryModel = gitRepositoryModel;
		putValue(Action.NAME, "Open Git Repository");
		putValue(Action.SMALL_ICON, uiContext.getIcon(IconType.OPEN));
		openRepositoryAction = new OpenRepositoryAction(uiContext,
				gitRepositoryModel, gitRepositoryAccess);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File gitRepoDir = new File(System.getProperty("user.dir"));

		if (gitRepositoryModel.getGitDir() != null) {
			gitRepoDir = gitRepositoryModel.getGitDir();
		}

		JFileChooser jFileChooser = new JFileChooser(gitRepoDir);
		jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = jFileChooser.showOpenDialog(null);
		if (JFileChooser.APPROVE_OPTION == result) {
			File selectedFile = jFileChooser.getSelectedFile();
			gitRepositoryModel.setGitDir(selectedFile);
			openRepositoryAction.actionPerformed(e);
		}
	}

}
