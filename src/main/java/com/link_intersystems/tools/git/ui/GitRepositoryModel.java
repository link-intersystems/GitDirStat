package com.link_intersystems.tools.git.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.link_intersystems.tools.git.domain.Ref;
import com.link_intersystems.tools.git.domain.TreeObject;

public class GitRepositoryModel extends AbstractPropertyChangeSupport {

	public static final String PROP_REPOSITORY_ID = "repositoryId";
	public static final String PROP_GIT_DIR = "gitDir";
	public static final String PROP_COMMIT_RANGE_TREE = "commitRangeTree";
	public static final String PROP_REFS = "refs";
	public static final String PROP_SELECTED_PATHS = "selectedPaths";
	public static final String PROP_SELECTED_REFS = "selectedRefs";

	private File gitDir;
	private String repositoryId;
	private TreeObject commitRangeTree;
	private List<Ref> refs = new ArrayList<Ref>();
	private List<Ref> selectedRefs = new ArrayList<Ref>();
	private List<String> selectedPaths = new ArrayList<String>();

	private RefsListModel refsListModel = new RefsListModel();


	public GitRepositoryModel() {
		gitDir = new File(System.getProperty("user.dir"));
	}

	public void setGitDir(File gitDir) {
		firePropertyChange(PROP_GIT_DIR, this.gitDir, this.gitDir = gitDir);
		setCommitRangeTree(null);
		List<Ref> refs = Collections.emptyList();
		setSelectedRefs(refs);
		setRefs(refs);
	}

	public boolean isGitDirSet() {
		return getGitDir() != null;
	}

	public File getGitDir() {
		return gitDir;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		firePropertyChange(PROP_REPOSITORY_ID, this.repositoryId,
				this.repositoryId = repositoryId);
	}

	public TreeObject getCommitRangeTree() {
		return commitRangeTree;
	}

	public void setCommitRangeTree(TreeObject commitRangeTree) {
		firePropertyChange(PROP_COMMIT_RANGE_TREE, this.commitRangeTree,
				this.commitRangeTree = commitRangeTree);
	}

	public void setSelectedRefs(List<Ref> selectedRefs) {
		List<? extends Ref> oldSelectedRefs = this.selectedRefs;
		this.selectedRefs = new ArrayList<Ref>(selectedRefs);
		this.selectedRefs = selectedRefs;
		firePropertyChange(PROP_SELECTED_REFS, oldSelectedRefs,
				this.selectedRefs);
	}

	public List<? extends Ref> getSelectedRefs() {
		return selectedRefs;
	}

	public List<? extends Ref> getRefs() {
		return Collections.unmodifiableList(this.refs);
	}

	public void setSelectedPaths(List<String> selectedPaths) {
		List<String> oldSelectedPaths = this.selectedPaths;
		this.selectedPaths = new ArrayList<String>(selectedPaths);
		firePropertyChange(PROP_REFS, oldSelectedPaths, this.selectedPaths);
	}

	public List<String> getSelectedPaths() {
		return Collections.unmodifiableList(selectedPaths);
	}

	public void setRefs(List<? extends Ref> refs) {
		List<Ref> oldRefs = this.refs;
		this.refs = new ArrayList<Ref>(refs);
		refsListModel.setList(this.refs);
		firePropertyChange(PROP_REFS, oldRefs, this.refs);

	}

	public RefsListModel getRefsListModel() {
		return refsListModel;
	}



}
