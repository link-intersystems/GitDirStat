package com.link_intersystems.tools.git.ui;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

public class OpenAction extends AbstractAction {

	/**
	 *
	 */
	private static final long serialVersionUID = 6082672924263782869L;
	private GitRepositoryModel gitRepositoryModel;
	private AbstractAction loadRepoAction;

	public OpenAction(GitRepositoryModel gitRepositoryModel,
			AbstractAction loadRepoAction) {
		this.gitRepositoryModel = gitRepositoryModel;
		this.loadRepoAction = loadRepoAction;
		putValue(Action.NAME, "Open Git Repository");
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
			loadRepoAction.actionPerformed(e);
		}
	}

}
