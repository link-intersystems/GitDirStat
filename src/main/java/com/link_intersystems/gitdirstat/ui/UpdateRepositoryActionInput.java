package com.link_intersystems.gitdirstat.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.link_intersystems.gitdirstat.domain.GitRepositoryAccess;
import com.link_intersystems.gitdirstat.domain.Ref;
import com.link_intersystems.swing.ActionInputSource;
import com.link_intersystems.swing.CheckboxRenderer;
import com.link_intersystems.swing.InvertSelectionAction;
import com.link_intersystems.swing.ListModelSelection;
import com.link_intersystems.swing.ListSelectionModelMemento;
import com.link_intersystems.swing.SelectAllAction;
import com.link_intersystems.swing.UnselectAllAction;

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

		JPanel selectRefsPanel = new JPanel(new BorderLayout());
		JScrollPane jScrollPane = new JScrollPane(jList);
		jScrollPane.setPreferredSize(new Dimension(320, 480));

		selectRefsPanel.add(jScrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		selectRefsPanel.add(buttonPanel, BorderLayout.SOUTH);

		SelectAllAction selectAllAction = new SelectAllAction(
				defaultListSelectionModel, refsListModel);
		selectAllAction.putValue(Action.NAME, "Select all");
		buttonPanel.add(new JButton(selectAllAction));

		UnselectAllAction unselectAllAction = new UnselectAllAction(
				defaultListSelectionModel);
		unselectAllAction.putValue(Action.NAME, "Unselect all");
		buttonPanel.add(new JButton(unselectAllAction));

		InvertSelectionAction invertSelectionAction = new InvertSelectionAction(
				defaultListSelectionModel, refsListModel);
		invertSelectionAction.putValue(Action.NAME, "Invert selection");
		buttonPanel.add(new JButton(invertSelectionAction));

		selectRefsPanel.setBorder(BorderFactory
				.createTitledBorder("Select refs"));

		Window mainFrame = this.uiContext.getMainFrame();
		int showOptionDialog = JOptionPane.showOptionDialog(mainFrame,
				selectRefsPanel, "Update", JOptionPane.OK_CANCEL_OPTION,
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
