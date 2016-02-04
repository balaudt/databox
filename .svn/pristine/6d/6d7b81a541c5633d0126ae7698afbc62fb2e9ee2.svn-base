package org.sagarius.radix.client.collect;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class LinkedListMultiMap<K, V> extends MultiMap<K, V> {
	public LinkedListMultiMap() {
		map = new LinkedHashMap<K, Collection<V>>();
	}

	@Override
	public void put(K key, V value) {
		Collection<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<V>();
			map.put(key, values);
		}
		values.add(value);
	}

	@Override
	public void putAll(K key, List<V> newValues) {
		Collection<V> values = map.get(key);
		if (values == null) {
			values = new LinkedList<V>();
			map.put(key, values);
		}
		values.addAll(newValues);
	}

}
