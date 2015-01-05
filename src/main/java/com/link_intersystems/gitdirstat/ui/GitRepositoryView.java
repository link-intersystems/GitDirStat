package com.link_intersystems.gitdirstat.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.link_intersystems.gitdirstat.ui.PathModel.SelectionSource;
import com.link_intersystems.gitdirstat.ui.treeobjects.GitRepositoryComponent;
import com.link_intersystems.gitdirstat.ui.treeobjects.TreeObjectsTableComponent;
import com.link_intersystems.gitdirstat.ui.treeobjects.TreeObjectsTreeComponent;
import com.link_intersystems.swing.RelativeLayout;
import com.link_intersystems.swing.RelativeLayout.RelativeConstraints;
import com.link_intersystems.swing.SimpleDocument;
import com.link_intersystems.swing.UnmodifiablePlainDocument;
import com.link_intersystems.swing.WeakReferencePropertyChangeSupport;

public class GitRepositoryView extends JPanel {

	private static final long serialVersionUID = -1113456173138142763L;
	public static final String PROP_VIEW_COMPONENT = "viewComponent";

	private WeakReferencePropertyChangeSupport weakReferencePropertyChangeSupport = new WeakReferencePropertyChangeSupport(
			this);

	private class SetViewComponentAction extends AbstractAction {

		private static final long serialVersionUID = -704010409655632786L;

		private GitRepositoryComponent gitRepositoryComponent;

		private GitRepositoryModel gitRepositoryModel;

		private SelectionSource selectionSource;

		public SetViewComponentAction(
				GitRepositoryComponent gitRepositoryComponent,
				SelectionSource selectionSource) {
			this.gitRepositoryComponent = gitRepositoryComponent;
			this.selectionSource = selectionSource;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (gitRepositoryModel != null) {
				PathModel pathModel = gitRepositoryModel.getPathModel();
				pathModel.setSelectionSource(selectionSource);
			}
			setViewComponent(gitRepositoryComponent);
			putValue(Action.SELECTED_KEY, true);
		}

		public void setGitRepositoryModel(GitRepositoryModel gitRepositoryModel) {
			this.gitRepositoryModel = gitRepositoryModel;
		}

	}

	private class GitRepositoryModelSync implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (GitRepositoryModel.PROP_GIT_DIR.equals(evt.getPropertyName())) {
				GitRepositoryModel gitRepositoryModel = (GitRepositoryModel) evt
						.getSource();
				updateGitDir(gitRepositoryModel);
			}
		}

		public void updateGitDir(GitRepositoryModel gitRepositoryModel) {
			File gitDir = gitRepositoryModel.getGitDir();
			SimpleDocument modifiable = unmodifiablePlainDocument
					.getModifiable();
			try {
				modifiable.setText(gitDir.getCanonicalPath());
			} catch (IOException e) {
				gitDir.getAbsolutePath();
			}
		}
	}

	private GitRepositoryModel gitRepositoryModel;

	private TreeObjectsTableComponent treeObjectsTableComponent = new TreeObjectsTableComponent();
	private TreeObjectsTreeComponent treeObjectsTreeComponent = new TreeObjectsTreeComponent();

	private JPanel rootPane = new JPanel();

	SetViewComponentAction setTableAction = new SetViewComponentAction(
			treeObjectsTableComponent, SelectionSource.LIST_SELECTION);
	SetViewComponentAction setTreeAction = new SetViewComponentAction(
			treeObjectsTreeComponent, SelectionSource.TREE_SELECTION);

	private GitRepositoryComponent viewComponent;
	private RelativeLayout relativeLayout = new RelativeLayout();
	private JPanel header = new JPanel(relativeLayout);
	private JTextField headerText = new JTextField("test");
	private JComponent mainComponent = rootPane;
	UnmodifiablePlainDocument unmodifiablePlainDocument = new UnmodifiablePlainDocument();

	private GitRepositoryModelSync gitRepositoryModelSync = new GitRepositoryModelSync();

	public GitRepositoryView() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.addPropertyChangeListener(weakReferencePropertyChangeSupport);

		add(rootPane, BorderLayout.CENTER);
		rootPane.setLayout(new BorderLayout());
		rootPane.add(header, BorderLayout.NORTH);
		relativeLayout.setFillLastComponent(true);

		headerText.setDocument(unmodifiablePlainDocument);
		header.add(headerText, new RelativeConstraints(0.5));
		setTableAction.actionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, "setTableAction"));
	}

	private void setViewComponent(GitRepositoryComponent gitRepositoryComponent) {
		GitRepositoryComponent oldView = this.viewComponent;
		if (this.viewComponent != null) {
			mainComponent.remove(this.viewComponent);
			this.viewComponent.afterInvisible();
		}

		this.viewComponent = gitRepositoryComponent;

		if (this.viewComponent != null) {
			this.viewComponent.beforeVisible();
			mainComponent.add(this.viewComponent, BorderLayout.CENTER);
		}

		firePropertyChange(PROP_VIEW_COMPONENT, oldView, this.viewComponent);

		mainComponent.invalidate();
		mainComponent.validate();
		mainComponent.repaint();
	}

	public GitRepositoryComponent getViewComponent() {
		return viewComponent;
	}

	public void setModel(GitRepositoryModel gitRepositoryModel) {
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.removePropertyChangeListener(gitRepositoryModelSync);
		}
		this.gitRepositoryModel = gitRepositoryModel;
		if (this.gitRepositoryModel != null) {
			this.gitRepositoryModel
					.addPropertyChangeListener(gitRepositoryModelSync);
			gitRepositoryModelSync.updateGitDir(this.gitRepositoryModel);

			treeObjectsTableComponent.setModel(gitRepositoryModel);
			treeObjectsTreeComponent.setModel(gitRepositoryModel);
			setTableAction.setGitRepositoryModel(gitRepositoryModel);
			setTreeAction.setGitRepositoryModel(gitRepositoryModel);
		}
		viewComponent.setModel(gitRepositoryModel);
	}

	public TreeObjectsTableComponent getSizeMetricsTableComponent() {
		return treeObjectsTableComponent;
	}

	public TreeObjectsTreeComponent getSizeMetricsTreeComponent() {
		return treeObjectsTreeComponent;
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

}
