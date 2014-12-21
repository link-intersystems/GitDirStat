package com.link_intersystems.tools.git.domain;

import java.util.Collection;

import com.link_intersystems.tools.git.CommitRange;

public interface CommitRangeTreeBuilder {

	public TreeObject build(Collection<CommitRange> commitRanges,
			ProgressListener progressListener);
}
