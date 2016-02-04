package org.sagarius.radix.client.collect;

import java.util.HashMap;

public class Maps {
	public static class ImmutableHashMap<K, V> {
		private HashMap<K, V> map = new HashMap<K, V>();

		public ImmutableHashMap<K, V> put(K key, V value) {
			map.put(key, value);
			return this;
		}

		public HashMap<K, V> build() {
			return map;
		}
	}
}
