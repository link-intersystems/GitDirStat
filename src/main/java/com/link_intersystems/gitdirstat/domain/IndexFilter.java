package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;

public interface IndexFilter {

	void apply(CommitUpdate commitUpdate) throws IOException;

}
