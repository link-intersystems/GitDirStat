package com.link_intersystems.tools.git.ui.config;

import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.link_intersystems.tools.git.domain.Ref;
import com.link_intersystems.tools.git.ui.GitRepositoryModel;

public class RepositoryConfigComponent extends JComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 7964655255960094798L;
	private JButton selectRefs;
	RepositoryConfigParams configParams;
	private DefaultListModel branchesModel = new DefaultListModel();
	private DefaultListSelectionModel branchSelectionModel = new DefaultListSelectionModel();

	private GitRepositorySynchronization gitRepoSynchronization = new GitRepositorySynchronization();
	private GitRepositoryModel gitRepositoryModel;

	public RepositoryConfigComponent(RepositoryConfigParams configParams) {
		this.configParams = configParams;
		setLayout(new FlowLayout(FlowLayout.LEFT));

		add(new JLabel("Branches:"));
		selectRefs = new JButton();
		selectRefs.setAction(new SelectRefsAction(configParams, branchesModel,
				branchSelectionModel));
		add(selectRefs);

		branchSelectionModel.addListSelectionListener(gitRepoSynchronization);
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.addPropertyChangeListener(gitRepoSynchronization);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.addPropertyChangeListener(gitRepoSynchronization);
		}
		updateBranchListModel();
	}

	private void updateBranchListModel() {
		branchesModel.clear();
		if (gitRepositoryModel != null) {
			List<? extends Ref> refs = gitRepositoryModel.getRefs();
			for (Ref ref : refs) {
				branchesModel.addElement(ref);
			}
		}
	}

	private class GitRepositorySynchronization implements
			PropertyChangeListener, ListSelectionListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (GitRepositoryModel.PROP_REFS.equals(propertyName)) {
				updateBranchListModel();
			} else if (GitRepositoryModel.PROP_SELECTED_REFS
					.equals(propertyName)) {
				if (gitRepositoryModel.getSelectedRefs().isEmpty()) {
					branchSelectionModel.clearSelection();
				}
			}
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			List<Ref> refs = new ArrayList<Ref>();

			int firstIndex = e.getFirstIndex();
			int lastIndex = e.getLastIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {
				if (branchSelectionModel.isSelectedIndex(i)) {
					Ref ref = (Ref) branchesModel.get(i);
					refs.add(ref);
				}
			}

			gitRepositoryModel.setSelectedRefs(refs);
		}

	}
}
