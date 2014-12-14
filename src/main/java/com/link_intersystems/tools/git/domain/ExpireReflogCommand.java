package com.link_intersystems.tools.git.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ReflogEntry;

public class ExpireReflogCommand extends GitCommand<Void> {

	private Date expireDate;
	private GitRepository gitRepository;

	protected ExpireReflogCommand(GitRepository gitRepository) {
		super(gitRepository.getRepository());
		this.gitRepository = gitRepository;
		setCallable(true);
	}

	public void setExpire(Date expireDate) {
		Date now = new Date();
		if (now.before(expireDate)) {
			throw new IllegalArgumentException(
					"expireDate must be in the past or null");
		}
		this.expireDate = expireDate;
	}

	@Override
	public Void call() throws GitAPIException {
		checkCallable();

		List<Ref> refs = gitRepository.getRefs(Ref.class);

		try {
			for (Ref ref : refs) {
				List<ReflogEntry> reflogEntries = ref.getReflogEntries();
				List<ReflogEntry> nonExpiredEntries = filterExpiredReflogEntries(reflogEntries);
				ref.clearReflog();
				ref.addReflogEntries(nonExpiredEntries);
			}
		} catch (IOException e) {
			throw new GitAPIException("Unable to read reflog entries", e) {
			};
		}

		return null;
	}

	private List<ReflogEntry> filterExpiredReflogEntries(
			List<ReflogEntry> reflogEntries) {
		List<ReflogEntry> nonExpired = new ArrayList<ReflogEntry>();

		for (ReflogEntry reflogEntry : reflogEntries) {
			PersonIdent who = reflogEntry.getWho();

			if (!isExpired(who)) {
				nonExpired.add(reflogEntry);
			}

		}
		return nonExpired;
	}

	private boolean isExpired(PersonIdent who) {
		if (expireDate == null) {
			return true;
		}

		Date when = who.getWhen();
		TimeZone timeZone = who.getTimeZone();

		return when.before(expireDate);
	}

}
