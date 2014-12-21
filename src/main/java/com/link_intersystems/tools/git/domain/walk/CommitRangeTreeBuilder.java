package com.link_intersystems.tools.git.domain.walk;

import java.util.Collection;

import com.link_intersystems.tools.git.CommitRange;
import com.link_intersystems.tools.git.domain.ProgressListener;
import com.link_intersystems.tools.git.domain.TreeObject;

public interface CommitRangeTreeBuilder {

	public TreeObject build(Collection<CommitRange> commitRanges,
			ProgressListener progressListener);
}
