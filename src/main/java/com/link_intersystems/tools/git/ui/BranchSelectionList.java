package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.link_intersystems.swing.CheckboxListCellRenderer;
import com.link_intersystems.tools.git.domain.Ref;

public class BranchSelectionList extends JPanel {

	private static final long serialVersionUID = 8285821725678169740L;

	private GitRepositorySynchronization gitRepositorySynchronization = new GitRepositorySynchronization();
	private GitRepositoryModel gitRepositoryModel;

	private DefaultListModel listModel = new DefaultListModel();
	private DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
	private JList selectionList = new JList(listModel);

	public BranchSelectionList() {
		setLayout(new BorderLayout());

		selectionList.setSelectionModel(selectionModel);
		selectionList.setCellRenderer(new CheckboxListCellRenderer());
		add(new JScrollPane(selectionList), BorderLayout.CENTER);
	}

	public Action createApplyBranchSelectionAction() {
		ApplyBranchSelectionAction applyBranchSelectionAction = new ApplyBranchSelectionAction();
		return applyBranchSelectionAction;
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.removePropertyChangeListener(gitRepositorySynchronization);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.addPropertyChangeListener(gitRepositorySynchronization);
		}
		updateBranchListModel();
	}

	private void updateBranchListModel() {
		Collection<Ref> selectedRefs = getSelectedRefs();
		listModel.clear();
		if (gitRepositoryModel != null) {
			List<? extends Ref> refs = gitRepositoryModel.getRefs();
			for (int i = 0; i < refs.size(); i++) {
				Ref ref = refs.get(i);
				listModel.addElement(ref);
				if (selectedRefs.contains(ref)) {
					selectionModel.addSelectionInterval(i, i);
				}
			}
		}
	}

	private Collection<Ref> getSelectedRefs() {
		Collection<Ref> selectedRefs = new HashSet<Ref>();
		int minIndex = selectionModel.getMinSelectionIndex();
		int maxIndex = selectionModel.getMaxSelectionIndex();
		for (int i = minIndex; i <= maxIndex; i++) {
			if (selectionModel.isSelectedIndex(i)) {
				Ref ref = (Ref) listModel.get(i);
				selectedRefs.add(ref);
			}
		}
		return selectedRefs;
	}

	private class ApplyBranchSelectionAction extends AbstractAction {

		private static final long serialVersionUID = 6047021841050442726L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<Ref> refs = new ArrayList<Ref>();

			ListSelectionModel selectionModel = selectionList
					.getSelectionModel();

			int firstIndex = selectionModel.getMinSelectionIndex();
			int lastIndex = selectionModel.getMaxSelectionIndex();

			for (int i = firstIndex; i <= lastIndex; i++) {
				if (selectionModel.isSelectedIndex(i)) {
					Ref ref = (Ref) listModel.get(i);
					refs.add(ref);
				}
			}

			gitRepositoryModel.setSelectedRefs(refs);
		}

	}

	private class GitRepositorySynchronization implements
			PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (GitRepositoryModel.PROP_REFS.equals(propertyName)) {
				updateBranchListModel();
			}
		}

	}

}
