package com.link_intersystems.tools.git.ui;

import java.awt.Component;

import javax.swing.JViewport;

import com.link_intersystems.tools.git.ui.metrics.GitRepositoryComponent;

public class GitRepositoryViewport extends JViewport {

	private static final long serialVersionUID = 2467948955477953464L;

	@Override
	public void setView(Component view) {
		if (!(view instanceof GitRepositoryComponent)) {
			throw new IllegalArgumentException("view must be a "
					+ GitRepositoryComponent.class.getName());
		}
		super.setView(view);
	}

	public GitRepositoryComponent getGitRepositoryComponent(){
		return (GitRepositoryComponent) getView();
	}
}
