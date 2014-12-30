package com.link_intersystems.gitdirstat.domain;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.AllPredicate;
import org.apache.commons.collections4.functors.FalsePredicate;
import org.apache.commons.collections4.functors.TruePredicate;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ReflogEntry;

import com.link_intersystems.lang.Assert;

public class ExpireReflogCommand extends GitCommand<Void> {

	private GitRepository gitRepository;
	private ReflogEntryFilter reflogEntryFilter = NullReflogEntryFilter.INSTANCE;
	private Predicate<ReflogEntry> expireDatePredicate = FalsePredicate
			.falsePredicate();

	protected ExpireReflogCommand(GitRepository gitRepository) {
		super(gitRepository.getRepository());
		this.gitRepository = gitRepository;
		setCallable(true);
	}

	public void setExpire(Date expireDate, TimeZone timeZone) {
		Assert.notNull("expireDate", expireDate);
		Assert.notNull("timeZone", timeZone);

		if (expireDate == null) {
			setExpire((Calendar) null);
		} else {
			Calendar expireCalendar = Calendar.getInstance(timeZone);
			expireCalendar.setTime(expireDate);

			setExpire(expireCalendar);
		}
	}

	public void setExpire(Calendar expireCalendar) {
		if (expireCalendar == null) {
			this.expireDatePredicate = TruePredicate.truePredicate();
		} else {
			Calendar nowCalendar = Calendar.getInstance();

			if (expireCalendar.after(nowCalendar)) {
				String message = MessageFormat
						.format("expire date {0,date,full} in time zone {1} must be in the"
								+ " past according to the current date {2,date,full} in"
								+ " time zone {3} or null",
								expireCalendar.getTime(),
								expireCalendar.getTimeZone(),
								nowCalendar.getTime(),
								nowCalendar.getTimeZone());

				throw new IllegalArgumentException(message);
			}
			this.expireDatePredicate = new ExpireDateFilterPredicate(
					expireCalendar);
		}
	}

	public void setReflogEntryFilter(ReflogEntryFilter reflogEntryFilter) {
		if (reflogEntryFilter == null) {
			reflogEntryFilter = NullReflogEntryFilter.INSTANCE;
		}
		this.reflogEntryFilter = reflogEntryFilter;
	}

	@Override
	public Void call() throws GitAPIException {
		checkCallable();

		List<Ref> refs = gitRepository.getRefs(Ref.class);

		List<Predicate<ReflogEntry>> refLogEntryFilterPredicates = new ArrayList<Predicate<ReflogEntry>>();

		refLogEntryFilterPredicates.add(expireDatePredicate);
		refLogEntryFilterPredicates.add(new ReflogEntryFilterPredicateAdapter(
				reflogEntryFilter));

		Predicate<ReflogEntry> refLogEntriesFilter = AllPredicate
				.allPredicate(refLogEntryFilterPredicates);

		try {
			for (Ref ref : refs) {
				List<ReflogEntry> reflogEntries = ref.getReflogEntries();

				Iterator<ReflogEntry> reflogEntryIterator = reflogEntries
						.iterator();
				Iterator<ReflogEntry> filteredReflogEntryIterator = IteratorUtils
						.filteredIterator(reflogEntryIterator,
								refLogEntriesFilter);
				List<ReflogEntry> nonFilteredReflogEntries = IteratorUtils
						.toList(filteredReflogEntryIterator);

				ref.clearReflog();
				ref.addReflogEntries(nonFilteredReflogEntries);
			}
		} catch (IOException e) {
			throw new GitAPIException("Unable to read reflog entries", e) {

				private static final long serialVersionUID = 2266798959465371684L;
			};
		}

		return null;
	}

	public static interface ReflogEntryFilter {

		public boolean accept(ReflogEntry reflogEntry);
	}

	private static class NullReflogEntryFilter implements ReflogEntryFilter {

		private static final NullReflogEntryFilter INSTANCE = new NullReflogEntryFilter();

		@Override
		public boolean accept(ReflogEntry reflogEntry) {
			return true;
		}

	}

	private static class ReflogEntryFilterPredicateAdapter implements
			Predicate<ReflogEntry> {

		private ReflogEntryFilter reflogEntryFilter;

		public ReflogEntryFilterPredicateAdapter(
				ReflogEntryFilter reflogEntryFilter) {
			this.reflogEntryFilter = reflogEntryFilter;
		}

		@Override
		public boolean evaluate(ReflogEntry reflogEntry) {
			return reflogEntryFilter.accept(reflogEntry);
		}
	}

	private static class ExpireDateFilterPredicate implements
			Predicate<ReflogEntry> {

		private Calendar expireCalendar;

		public ExpireDateFilterPredicate(Calendar expireCalendar) {
			this.expireCalendar = expireCalendar;
		}

		@Override
		public boolean evaluate(ReflogEntry reflogEntry) {
			PersonIdent who = reflogEntry.getWho();

			TimeZone timeZone = who.getTimeZone();
			Calendar reflogEntryCalendar = Calendar.getInstance(timeZone);
			Date when = who.getWhen();
			reflogEntryCalendar.setTime(when);

			boolean isNotExpired = reflogEntryCalendar.after(expireCalendar);
			return isNotExpired;
		}

	}
}
