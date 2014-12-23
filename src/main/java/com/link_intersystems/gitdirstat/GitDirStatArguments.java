package com.link_intersystems.gitdirstat;

import java.io.File;
import java.io.OutputStream;

import com.link_intersystems.gitdirstat.common.SortOrder;
import com.link_intersystems.gitdirstat.domain.TreeObjectSortBy;

public interface GitDirStatArguments {

	public File getGitRepositoryDir();

	public OutputStream getOutputStream();

	public SortOrder getSortOrder();

	public TreeObjectSortBy getSortBy();

}
