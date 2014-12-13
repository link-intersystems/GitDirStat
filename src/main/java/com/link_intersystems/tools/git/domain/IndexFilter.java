package com.link_intersystems.tools.git.domain;

import java.io.IOException;

public interface IndexFilter {

	void apply(CommitUpdate commitUpdate) throws IOException;

}
