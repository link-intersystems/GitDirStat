package com.link_intersystems.tools.git.ui;

import java.io.File;
import java.util.List;

import javax.swing.Action;

import com.link_intersystems.tools.git.domain.Branch;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.Ref;
import com.link_intersystems.tools.git.domain.RemoteBranch;

public class UpdateRefsAction extends AsyncAction<List<? extends Ref>, Void> {

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
	protected List<? extends Ref> doInBackground() {
		GitRepository gitRepository = null;

		File gitDir = gitRepositoryModel.getGitDir();
		gitRepository = gitRepositoryAccess.getGitRepository(gitDir);

		List<Branch> refs = gitRepository.getRefs(Branch.class);
		return refs;
	}

	@Override
	protected void done(List<? extends Ref> result) {
		gitRepositoryModel.setRefs(result);
	}

}
