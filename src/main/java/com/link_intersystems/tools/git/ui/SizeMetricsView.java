package com.link_intersystems.tools.git.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.link_intersystems.swing.WeakReferencePropertyChangeSupport;
import com.link_intersystems.tools.git.ui.metrics.GitRepositoryComponent;
import com.link_intersystems.tools.git.ui.metrics.SizeMetricsTableComponent;
import com.link_intersystems.tools.git.ui.metrics.SizeMetricsTreeComponent;

public class SizeMetricsView extends JPanel {

	private static final long serialVersionUID = -1113456173138142763L;
	public static final String PROP_VIEW_COMPONENT = "viewComponent";

	private WeakReferencePropertyChangeSupport weakReferencePropertyChangeSupport = new WeakReferencePropertyChangeSupport(
			this);

	private class SetViewComponentAction extends AbstractAction {

		private static final long serialVersionUID = -704010409655632786L;

		private GitRepositoryComponent gitRepositoryComponent;

		public SetViewComponentAction(
				GitRepositoryComponent gitRepositoryComponent) {
			this.gitRepositoryComponent = gitRepositoryComponent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setViewComponent(gitRepositoryComponent);
			putValue(Action.SELECTED_KEY, true);

		}

	}

	private GitRepositoryModel gitRepositoryModel = new GitRepositoryModel();
	private BranchSelectionTable branchSelectionList = new BranchSelectionTable();

	private SizeMetricsTableComponent sizeMetricsTableComponent = new SizeMetricsTableComponent();
	private SizeMetricsTreeComponent sizeMetricsTreeComponent = new SizeMetricsTreeComponent();

	SetViewComponentAction setTableAction = new SetViewComponentAction(
			sizeMetricsTableComponent);
	SetViewComponentAction setTreeAction = new SetViewComponentAction(
			sizeMetricsTreeComponent);

	private GitRepositoryComponent viewComponent;
	private JSplitPane jSplitPane;

	public SizeMetricsView() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		branchSelectionList.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(), "Branches"));

		jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				branchSelectionList, new JPanel());
		add(jSplitPane, BorderLayout.CENTER);
		this.addPropertyChangeListener(weakReferencePropertyChangeSupport);

		setTreeAction.actionPerformed(null);
	}

	private void setViewComponent(GitRepositoryComponent gitRepositoryComponent) {
		GitRepositoryComponent oldView = this.viewComponent;
		if (this.viewComponent != null) {
			this.viewComponent.setModel(null);
			jSplitPane.remove(gitRepositoryComponent);
		}

		this.viewComponent = gitRepositoryComponent;

		if (this.viewComponent != null) {
			this.viewComponent.setModel(gitRepositoryModel);
			jSplitPane.setRightComponent(this.viewComponent);
		}

		firePropertyChange(PROP_VIEW_COMPONENT, oldView, this.viewComponent);

		invalidate();
		validate();
		repaint();
	}

	public GitRepositoryComponent getViewComponent() {
		return viewComponent;
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		this.gitRepositoryModel = gitRepositoryModel;
		branchSelectionList.setModel(gitRepositoryModel);
		viewComponent.setModel(gitRepositoryModel);
	}

	public SizeMetricsTableComponent getSizeMetricsTableComponent() {
		return sizeMetricsTableComponent;
	}

	public SizeMetricsTreeComponent getSizeMetricsTreeComponent() {
		return sizeMetricsTreeComponent;
	}

	public Action getSetTableAction() {
		return setTableAction;
	}

	public Action getSetTreeAction() {
		return setTreeAction;
	}

	public void addWeakPropertyChangeListener(PropertyChangeListener listener) {
		weakReferencePropertyChangeSupport
				.addWeakReferencePropertyChangeListener(listener);
	}

	public Action createApplyBranchSelectionAction() {
		return branchSelectionList.createApplyBranchSelectionAction();
	}

}
