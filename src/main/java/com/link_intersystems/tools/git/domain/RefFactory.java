package com.link_intersystems.tools.git.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;

public class RefFactory {

	static final String PREFIX_LOCAL = "refs/heads/";
	static final String PREFIX_REMOTE = "refs/remotes/";
	static final String PREFIX_TAG = "refs/tags";

	private GitRepository gitRepository;

	public RefFactory(GitRepository gitRepository) {
		this.gitRepository = gitRepository;
	}

	public List<String> getRefPrefixes(Class<? extends Ref> refType) {
		List<String> refPrefixes = new ArrayList<String>();
		if (Ref.class.equals(refType)) {
			refPrefixes.add(RefDatabase.ALL);
		} else if (Branch.class.equals(refType)) {
			refPrefixes.addAll(getLocalBranchRefs());
			refPrefixes.addAll(getRemoteBranchRefs());
		} else if (LocalBranch.class.equals(refType)) {
			refPrefixes.addAll(getLocalBranchRefs());
		} else if (RemoteBranch.class.equals(refType)) {
			refPrefixes.addAll(getRemoteBranchRefs());
		}
		return refPrefixes;
	}

	private List<String> getRemoteBranchRefs() {
		List<String> remoteBranchRefs = new ArrayList<String>();
		Repository repository = gitRepository.getRepository();
		Set<String> remoteNames = repository.getRemoteNames();
		for (String remoteName : remoteNames) {
			remoteBranchRefs.add(PREFIX_REMOTE + remoteName + "/");
		}
		return remoteBranchRefs;
	}

	private List<String> getLocalBranchRefs() {
		return Collections.singletonList(PREFIX_LOCAL);
	}

	@SuppressWarnings("unchecked")
	public <T extends Ref> T create(org.eclipse.jgit.lib.Ref jgitRef) {
		Ref ref = null;
		String name = jgitRef.getName();
		if (name.startsWith(PREFIX_LOCAL)) {
			ref = new LocalBranch(jgitRef);
		} else if (name.startsWith(PREFIX_REMOTE)) {
			ref = new RemoteBranch(jgitRef);
		} else if (name.startsWith(PREFIX_TAG)) {
			ref = new Tag(jgitRef);
		} else if (jgitRef.isSymbolic()) {
			ref = new SymbolicRef(jgitRef);
		} else {
			throw new UnsupportedOperationException(jgitRef
					+ " not implemented yet");
		}

		return (T) ref;
	}
}
