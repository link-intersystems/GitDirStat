package com.link_intersystems.tools.git.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.comparators.ReverseComparator;

public class SortedMap<K, V> extends LinkedHashMap<K, V> {

	/**
	 *
	 */
	private static final long serialVersionUID = -5209192456981912398L;

	public static enum SortBy {
		KEY, VALUE;
	}

	public static enum SortOrder {
		ASC, DESC;
	}

	public SortedMap(Map<K, V> map) {
		this(map, SortBy.KEY, SortOrder.DESC);
	}

	public SortedMap(Map<K, V> map, SortBy sortBy, SortOrder sortOrder) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());

		Comparator<Map.Entry<K, V>> c = getMapEntryComparator(sortBy);

		if (SortOrder.DESC.equals(sortOrder)) {
			c = new ReverseComparator<Map.Entry<K, V>>(c);
		}

		Collections.sort(list, c);

		for (Iterator<Map.Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = it.next();
			put(entry.getKey(), entry.getValue());
		}
	}

	private Comparator<Map.Entry<K, V>> getMapEntryComparator(SortBy sortBy) {
		Comparator<Map.Entry<K, V>> c = null;
		if (SortBy.VALUE.equals(sortBy)) {
			c = new MapEntryValueSorter<K, V, Map.Entry<K, V>>();
		} else {
			c = new MapEntryKeySorter<K, V, Map.Entry<K, V>>();
		}
		return c;
	}

	private static class MapEntryValueSorter<K, V, E extends Map.Entry<K, V>>
			implements Comparator<E> {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compare(E o1, E o2) {
			return ((Comparable) (o1).getValue()).compareTo((o2).getValue());
		}

	}

	private static class MapEntryKeySorter<K, V, E extends Map.Entry<K, V>>
			implements Comparator<E> {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public int compare(E o1, E o2) {
			return ((Comparable) (o1).getKey()).compareTo((o2).getKey());
		}

	}
}
