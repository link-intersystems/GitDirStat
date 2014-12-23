package com.link_intersystems.gitdirstat;

import java.io.File;
import java.io.OutputStream;

import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;
import com.link_intersystems.util.SortOrder;

public interface GitDirStatArguments {

	public File getGitRepositoryDir();

	public OutputStream getOutputStream();

	public SortOrder getSortOrder();

	public TreeObjectSortBy getSortBy();

}
