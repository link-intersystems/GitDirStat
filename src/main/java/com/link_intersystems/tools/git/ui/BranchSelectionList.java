package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.link_intersystems.swing.CheckboxRenderer;

public class BranchSelectionList extends JPanel {

	private static final long serialVersionUID = 8285821725678169740L;

	private GitRepositoryModel gitRepositoryModel;

	private DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
	private JList selectionList = new JList();

	public BranchSelectionList() {
		setLayout(new BorderLayout());

		selectionList.setSelectionModel(selectionModel);
		selectionList.setCellRenderer(new CheckboxRenderer());
		add(new JScrollPane(selectionList), BorderLayout.CENTER);
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			RefsListModel refsListModel = gitRepositoryModel.getRefsListModel();
			selectionList.setModel(refsListModel);
			selectionList.setSelectionModel(refsListModel
					.getListSelectionModel());
		}
	}

}
