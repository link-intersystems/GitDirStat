package com.link_intersystems.gitdirstat.ui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;

public class GUIApplication {

	public void run(GitDirStatUIArguments dirStatUIArguments) throws Exception {
		setLookAndFeel();
		GitRepositoryAccess repoAccess = new GitRepositoryAccess();
		MainFrame mainFrame = new MainFrame(dirStatUIArguments, repoAccess);
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
