package com.link_intersystems.tools.git.service;

import com.link_intersystems.tools.git.service.GitRepositoryService.SortOrder;

public class GetSizeMetricsRequest {
	public String repositoryId;
	public SortOrder sortOrder;
	public ProgressListener progressListener;
	public String revstr;

	public GetSizeMetricsRequest(String repositoryId, String revstart,
			ProgressListener progressListener) {
		this(repositoryId, revstart, SortOrder.DESC, progressListener);
	}

	public GetSizeMetricsRequest(String repositoryId, String revstart,
			SortOrder sortOrder, ProgressListener progressListener) {
		this.repositoryId = repositoryId;
		this.revstr = revstart;
		this.sortOrder = sortOrder;
		this.progressListener = progressListener;
	}
}