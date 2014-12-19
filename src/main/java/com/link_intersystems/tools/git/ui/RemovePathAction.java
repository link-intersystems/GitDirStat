package com.link_intersystems.tools.git.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.tools.git.domain.CommitUpdate;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.IndexFilter;
import com.link_intersystems.tools.git.domain.TreeFileUpdate;
import com.link_intersystems.tools.git.domain.TreeUpdate;

public class RemovePathAction extends AsyncProgressAction<Void, Void> {

	private static final long serialVersionUID = -2409080673565317180L;
	private GitRepositoryModel gitRepositoryModel;
	private ProgressMonitor progressMonitor;
	private GitRepositoryAccess gitRepositoryAccess;

	public RemovePathAction(GitRepositoryModel gitRepositoryModel,
			GitRepositoryAccess gitRepositoryAccess,
			ProgressMonitor progressMonitor) {
		this.gitRepositoryModel = gitRepositoryModel;
		this.gitRepositoryAccess = gitRepositoryAccess;
		this.progressMonitor = progressMonitor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressMonitor, "Removing paths from history");
		File gitDir = gitRepositoryModel.getGitDir();
		GitRepository gitRepository = gitRepositoryAccess
				.getGitRepository(gitDir);
		final List<String> selectedPaths = gitRepositoryModel
				.getSelectedPaths();
		IndexFilter pathFilter = new IndexFilter() {

			@Override
			public void apply(CommitUpdate commitUpdate) throws IOException {
				TreeUpdate treeUpdate = commitUpdate.getTreeUpdate();
				while (treeUpdate.hasNext()) {
					TreeFileUpdate fileUpdate = treeUpdate.next();
					String path = fileUpdate.getPath();
					if (selectedPaths.contains(path)) {
						fileUpdate.delete();
					}
				}
			}
		};
		gitRepository.applyFilter(pathFilter, progressListenerMonitorAdapter);
		return null;
	}

}
