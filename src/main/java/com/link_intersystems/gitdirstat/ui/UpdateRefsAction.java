package com.link_intersystems.gitdirstat.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.Action;

import com.link_intersystems.gitdirstat.domain.GitRepository;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.swing.ProgressAction;
import com.link_intersystems.swing.ProgressMonitor;

public class UpdateRefsAction extends ProgressAction {

	private static final long serialVersionUID = 6082672924263782869L;

	private GitRepositoryAccess gitRepositoryAccess;
	private GitRepositoryModel gitRepositoryModel;

	public UpdateRefsAction(GitRepositoryAccess gitRepositoryAccess,
			GitRepositoryModel gitRepositoryModel) {
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.gitRepositoryModel = gitRepositoryModel;
		putValue(Action.NAME, "Update References");
	}

	@Override
	protected void doActionPerformed(ActionEvent e,
			ProgressMonitor progressMonitor) {
		GitRepository gitRepository = null;

		File gitDir = gitRepositoryModel.getGitDir();
		gitRepository = gitRepositoryAccess.getGitRepository(gitDir);

		List<Ref> refs = gitRepository.getRefs(Ref.class);
		RefsListModel refsListModel = gitRepositoryModel.getRefsListModel();
		refsListModel.setList(refs);
	}
}
