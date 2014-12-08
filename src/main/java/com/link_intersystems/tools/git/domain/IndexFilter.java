package com.link_intersystems.tools.git.domain;

import java.io.IOException;

public interface IndexFilter {

	void filter(Index index) throws IOException;

}
