package com.link_intersystems.tools.git;

import java.io.File;
import java.io.OutputStream;

import com.link_intersystems.tools.git.common.SortOrder;
import com.link_intersystems.tools.git.domain.TreeObjectSortBy;

public interface GitDirStatArguments {

	public File getGitRepositoryDir();

	public OutputStream getOutputStream();

	public SortOrder getSortOrder();

	public TreeObjectSortBy getSortBy();

}
