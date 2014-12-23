package com.link_intersystems.gitdirstat.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.link_intersystems.gitdirstat.domain.CommitUpdate;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.TreeFileUpdate;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeUpdate;
import com.link_intersystems.swing.ActionInputSource;

public class RemovePathsActionInput implements ActionInputSource<IndexFilter> {

	private GitRepositoryModel gitRepositoryModel;
	private UIContext uiContext;

	public RemovePathsActionInput(GitRepositoryModel gitRepositoryModel,
			UIContext uiContext, GitRepositoryAccess gitRepositoryAccess) {
		this.uiContext = uiContext;
		this.gitRepositoryModel = gitRepositoryModel;
	}

	@Override
	public IndexFilter getActionInput(ActionEvent e) {
		IndexFilter pathFilter = null;

		Window mainFrame = this.uiContext.getMainFrame();
		int showOptionDialog = JOptionPane
				.showConfirmDialog(
						mainFrame,
						"Do you want to remove the selected paths from the git repository?\nRemoved paths can not be recovered",
						"Rewrite Git History", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE);

		if (showOptionDialog == JOptionPane.OK_OPTION) {
			pathFilter = createPathFilter();
		}

		return pathFilter;
	}

	private IndexFilter createPathFilter() {
		List<TreeObject> selectedTreeObjects = gitRepositoryModel
				.getPathListModel().getSelectionModel().getSelection();
		final List<String> selectedPaths = new ArrayList<String>();
		for (TreeObject treeObject : selectedTreeObjects) {
			selectedPaths.add(treeObject.getRootRelativePath().getPathname());
		}
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
		return pathFilter;
	}
}
