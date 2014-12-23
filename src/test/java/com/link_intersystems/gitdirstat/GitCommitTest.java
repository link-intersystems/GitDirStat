package com.link_intersystems.gitdirstat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Test;

public class GitCommitTest {


	@Test
	public void tet() throws GitAPIException, IOException{
		File repoDir = new File("target/tmpGitRepo");
		InitCommand init = Git.init();
		init.setDirectory(repoDir);
		Git git = init.call();


		File testTxt = new File(repoDir, "someDir/test.txt");
		FileUtils.write(testTxt, "Hello\nWorld");

		AddCommand addCommand = git.add();
		addCommand.addFilepattern(".*");
		addCommand.call();

		CommitCommand commit = git.commit();
		commit.setMessage("Added text.txt");
		commit.call();

	}
}
