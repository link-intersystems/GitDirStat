package com.link_intersystems.gitdirstat.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import com.link_intersystems.gitdirstat.domain.CommitUpdate;
import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.IndexFilter;
import com.link_intersystems.gitdirstat.domain.TreeFileUpdate;
import com.link_intersystems.gitdirstat.domain.TreeObject;
import com.link_intersystems.gitdirstat.domain.TreeUpdate;
import com.link_intersystems.swing.ActionInputSource;
import com.link_intersystems.swing.SelectionModel;
import com.link_intersystems.swing.SelectionModelBasedListModel;

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

		PathModel pathModel = gitRepositoryModel.getPathModel();
		SelectionModel<TreeObject> selectionModel = pathModel
				.getSelectionModel();
		SelectionModelBasedListModel selectionModelBasedListModel = new SelectionModelBasedListModel();
		selectionModelBasedListModel.setSelectionModel(selectionModel);

		List<Object> dialogContent = new ArrayList<Object>();
		JList selectedPathList = new JList(selectionModelBasedListModel);
		selectedPathList.setCellRenderer(new TreeObjectPathnameListRenderer());
		JScrollPane selectedPathScrollPane = new JScrollPane(selectedPathList);
		selectedPathScrollPane.setPreferredSize(calculatePreferredSize(
				selectedPathList, selectionModel));
		dialogContent.add("You have selected the following paths for removal");
		dialogContent.add(selectedPathScrollPane);

		dialogContent.add("Removed paths can not be recovered anymore");
		dialogContent.add("Do you want to remove the selected paths now?");

		Window mainFrame = this.uiContext.getMainFrame();
		Object[] content = (Object[]) dialogContent
				.toArray(new Object[dialogContent.size()]);
		int showOptionDialog = JOptionPane.showConfirmDialog(mainFrame,
				content, "Rewrite Git History", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (showOptionDialog == JOptionPane.YES_OPTION) {
			pathFilter = createPathFilter();
		}

		return pathFilter;
	}

	private Dimension calculatePreferredSize(JList list,
			SelectionModel<TreeObject> selectionModel) {
		ListCellRenderer cellRenderer = list.getCellRenderer();

		int maxWidth = 480;
		int maxHeight = 160;
		int preferredWidth = 0;
		int preferredHeight = 0;

		List<TreeObject> selection = selectionModel.getSelection();
		for (int i = 0; i < selection.size(); i++) {
			TreeObject treeObject = selection.get(i);
			Component rendererComponent = cellRenderer
					.getListCellRendererComponent(list, treeObject, i, false,
							false);
			Dimension preferredSize = rendererComponent.getPreferredSize();

			preferredWidth = Math.max(preferredWidth,
					(int) preferredSize.getWidth());
			preferredWidth = Math.min(preferredWidth, maxWidth);

			preferredHeight = Math.max(preferredHeight, preferredHeight
					+ (int) preferredSize.getHeight());
			preferredHeight = Math.min(preferredHeight, maxHeight);
		}
		return new Dimension(preferredWidth, preferredHeight);
	}

	private IndexFilter createPathFilter() {
		List<TreeObject> selectedTreeObjects = gitRepositoryModel
				.getPathModel().getSelectionModel().getSelection();
		final List<String> selectedPaths = new ArrayList<String>();
		for (TreeObject treeObject : selectedTreeObjects) {
			List<TreeObject> fileList = treeObject.toFileList();
			for (TreeObject file : fileList) {
				selectedPaths.add(file.getRootRelativePath().getPathname());
			}
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

	private static class TreeObjectPathnameListRenderer extends
			DefaultListCellRenderer {

		private static final long serialVersionUID = 4377056356154468058L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			TreeObject treeObject = (TreeObject) value;
			String pathname = treeObject.getRootRelativePath().getPathname();
			return super.getListCellRendererComponent(list, pathname, index,
					isSelected, cellHasFocus);
		}
	}
}
