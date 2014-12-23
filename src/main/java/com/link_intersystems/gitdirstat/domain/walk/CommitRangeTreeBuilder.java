package com.link_intersystems.gitdirstat.domain.walk;

import java.util.Collection;

import com.link_intersystems.gitdirstat.domain.CommitRange;
import com.link_intersystems.gitdirstat.domain.ProgressListener;
import com.link_intersystems.gitdirstat.domain.TreeObject;

public interface CommitRangeTreeBuilder {

	public TreeObject build(Collection<CommitRange> commitRanges,
			ProgressListener progressListener);
}
