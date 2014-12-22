package com.link_intersystems.tools.git.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.link_intersystems.io.FileUtils;
import com.link_intersystems.swing.AsyncProgressAction;
import com.link_intersystems.swing.CheckboxRenderer;
import com.link_intersystems.swing.ListModelSelection;
import com.link_intersystems.swing.ListSelectionModelMemento;
import com.link_intersystems.swing.ProgressMonitor;
import com.link_intersystems.tools.git.domain.GitRepository;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.Ref;
import com.link_intersystems.tools.git.domain.TreeObject;

public class UpdateRepositoryAction extends
		AsyncProgressAction<List<? extends Ref>, Void, TreeObject> {

	private static final long serialVersionUID = -6024006313949630749L;
	private UIContext uiContext;
	private GitRepositoryModel gitRepositoryModel;
	private GitRepositoryAccess gitRepositoryAccess;

	public UpdateRepositoryAction(UIContext uiContext,
			GitRepositoryModel gitRepositoryModel,
			GitRepositoryAccess gitRepositoryAccess) {
		this.uiContext = uiContext;
		this.gitRepositoryModel = gitRepositoryModel;
		this.gitRepositoryAccess = gitRepositoryAccess;
	}

	@Override
	protected List<? extends Ref> getInput(ActionEvent e) {
		List<? extends Ref> refs = null;
		RefsListModel refsListModel = gitRepositoryModel.getRefsListModel();
		JList jList = new JList(refsListModel);
		jList.setCellRenderer(new CheckboxRenderer());
		ListSelectionModelMemento listSelectionModelMemento = new ListSelectionModelMemento();
		listSelectionModelMemento.save(refsListModel.getListSelectionModel());

		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		DefaultListSelectionModel defaultListSelectionModel = new DefaultListSelectionModel();
		listSelectionModelMemento.restore(defaultListSelectionModel);
		jList.setSelectionModel(defaultListSelectionModel);

		JScrollPane jScrollPane = new JScrollPane(jList);
		jScrollPane.setPreferredSize(new Dimension(320, 480));

		Window mainFrame = this.uiContext.getMainFrame();
		int showOptionDialog = JOptionPane.showOptionDialog(mainFrame,
				jScrollPane, "Select branches", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (showOptionDialog == JOptionPane.OK_OPTION) {
			listSelectionModelMemento.save(defaultListSelectionModel);
			listSelectionModelMemento.restore(refsListModel
					.getListSelectionModel());

			ListModelSelection<? extends Ref> selectionModel = refsListModel
					.getSelectionModel();
			refs = selectionModel.getSelection();
		}

		return refs;
	}

	@Override
	protected TreeObject doInBackground(List<? extends Ref> refs,
			ProgressMonitor progressMonitor) throws Exception {
		GitRepository gitRepository = null;

		File gitDir = gitRepositoryModel.getGitDir();
		gitRepository = gitRepositoryAccess.getGitRepository(gitDir);

		List<? extends Ref> selectedRefs = refs;

		String progressMessage = MessageFormat.format(
				"Analyzing repository: {0}",
				FileUtils.abbreviatedPath(gitDir, 50));
		ProgressListenerMonitorAdapter progressListenerMonitorAdapter = new ProgressListenerMonitorAdapter(
				progressMonitor, progressMessage);
		progressListenerMonitorAdapter.setUpdateInterval(250);
		TreeObject commitRangeTree = gitRepository.getCommitRangeTree(
				selectedRefs, progressListenerMonitorAdapter);
		return commitRangeTree;
	}

	@Override
	protected void done(TreeObject result) {
		gitRepositoryModel.setCommitRangeTree(result);
	}

}