package com.link_intersystems.gitdirstat.domain.walk;

import java.io.IOException;

public interface RevWalkTemplate {

	public abstract void setRevWalkConfigurer(
			RevWalkConfigurer revWalkConfigurer);

	public abstract void setRevCommitFilter(RevCommitFilter revCommitFilter);

	public abstract void walk(RevCommitWalk revCommitWalk) throws IOException;

}