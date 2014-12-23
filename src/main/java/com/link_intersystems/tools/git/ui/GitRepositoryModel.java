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

	private RefsListModel refsListModel = new RefsListModel();
	private PathListModel pathListModel = new PathListModel();

	public GitRepositoryModel() {
		gitDir = new File(System.getProperty("user.dir"));
	}

	public void setGitDir(File gitDir) {
		firePropertyChange(PROP_GIT_DIR, this.gitDir, this.gitDir = gitDir);
		setCommitRangeTree(null);
		List<Ref> refs = Collections.emptyList();
		refsListModel.setList(refs);
		List<TreeObject> paths = Collections.emptyList();
		pathListModel.setList(paths);
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
		List<TreeObject> pathList = new ArrayList<TreeObject>();
		if (commitRangeTree != null) {
			pathList.addAll(commitRangeTree.toFileList());

		}
		this.pathListModel.setList(pathList);
	}

	public List<? extends Ref> getRefs() {
		return Collections.unmodifiableList(this.refs);
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

	public PathListModel getPathListModel() {
		return pathListModel;
	}

}
