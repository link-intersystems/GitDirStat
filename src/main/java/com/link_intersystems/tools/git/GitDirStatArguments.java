package com.link_intersystems.tools.git;

import java.io.File;
import java.io.OutputStream;

public interface GitDirStatArguments {

	public File getGitRepositoryDir();

	public OutputStream getOutputStream();

}
