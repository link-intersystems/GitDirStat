package com.link_intersystems.tools.git;

import java.io.File;
import java.io.OutputStream;

import com.link_intersystems.tools.git.common.SortedMap.SortOrder;
import com.link_intersystems.tools.git.domain.TreeObjectSortOrder;

public interface GitDirStatArguments {

	public File getGitRepositoryDir();

	public OutputStream getOutputStream();

	public SortOrder getSortOrder();

	public TreeObjectSortOrder getSortBy();

}
