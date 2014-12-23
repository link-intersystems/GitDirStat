package com.link_intersystems.gitdirstat.ui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.link_intersystems.gitdirstat.GitDirStatApplication;
import com.link_intersystems.gitdirstat.GitDirStatArguments;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;

public class GUIApplication implements GitDirStatApplication {

	@Override
	public void run(GitDirStatArguments gitDirStatArguments) throws Exception {
		setLookAndFeel();
		GitRepositoryAccess repoAccess = new GitRepositoryAccess();
		MainFrame mainFrame = new MainFrame(gitDirStatArguments, repoAccess);
		mainFrame.setVisible(true);
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
