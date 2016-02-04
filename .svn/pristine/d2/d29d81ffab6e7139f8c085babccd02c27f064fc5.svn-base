package org.sagarius.radix.client.collect;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MultiMap<K, V> implements Map<K, Collection<V>> {

	protected Map<K, Collection<V>> map;

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, Collection<V>>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Collection<V> get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<V> put(K key, Collection<V> value) {
		return map.put(key, value);
	};

	@Override
	public void putAll(Map<? extends K, ? extends Collection<V>> m) {
		map.putAll(m);
	}

	@Override
	public Collection<V> remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Collection<V>> values() {
		return map.values();
	}

	public abstract void put(K key, V value);

	public int size(K key){
		Collection<V> values = map.get(key);
		return values == null ? -1 : values.size();
	}

	public void remove(K key, V value) {
		Collection<V> values = map.get(key);
		if (values == null) {
			return;
		}
		values.remove(value);
	}

	public abstract void putAll(K key, List<V> newValues);

	public boolean isEmpty(K key){
		Collection<V> values = map.get(key);
		return values == null ? true : values.isEmpty();
	}
}
