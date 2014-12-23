package com.link_intersystems.tools.git.ui;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.link_intersystems.swing.ActionInputSource;
import com.link_intersystems.swing.CheckboxRenderer;
import com.link_intersystems.swing.ListModelSelection;
import com.link_intersystems.swing.ListSelectionModelMemento;
import com.link_intersystems.tools.git.domain.GitRepositoryAccess;
import com.link_intersystems.tools.git.domain.Ref;

public class UpdateRepositoryActionInput implements
		ActionInputSource<List<? extends Ref>> {

	private GitRepositoryModel gitRepositoryModel;
	private UIContext uiContext;
	private UpdateRefsAction updateRefsAction;

	public UpdateRepositoryActionInput(GitRepositoryModel gitRepositoryModel,
			UIContext uiContext, GitRepositoryAccess gitRepositoryAccess) {
		this.uiContext = uiContext;
		this.gitRepositoryModel = gitRepositoryModel;
		updateRefsAction = new UpdateRefsAction(gitRepositoryAccess,
				gitRepositoryModel);
	}

	@Override
	public List<? extends Ref> getActionInput(ActionEvent e) {
		updateRefsAction.actionPerformed(null);
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
		jScrollPane.setBorder(BorderFactory.createTitledBorder("Select refs"));

		Window mainFrame = this.uiContext.getMainFrame();
		int showOptionDialog = JOptionPane.showOptionDialog(mainFrame,
				jScrollPane, "Update Repository", JOptionPane.OK_CANCEL_OPTION,
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
}
