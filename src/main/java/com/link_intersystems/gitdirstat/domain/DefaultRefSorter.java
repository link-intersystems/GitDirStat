package com.link_intersystems.gitdirstat.domain;

import java.util.Comparator;

public class DefaultRefSorter implements Comparator<Ref> {

	public static final DefaultRefSorter INSTANCE = new DefaultRefSorter();

	@Override
	public int compare(Ref o1, Ref o2) {
		String name1 = o1.getName();
		String name2 = o2.getSimpleName();

		if (name1.startsWith(RefFactory.PREFIX_LOCAL)) {
			if (name2.startsWith(RefFactory.PREFIX_LOCAL)) {
				return name1.compareTo(name2);
			} else {
				return -1;
			}
		} else {
			if (name2.startsWith(RefFactory.PREFIX_LOCAL)) {
				return 1;
			} else {
				return name1.compareTo(name2);
			}
		}
	}

}
