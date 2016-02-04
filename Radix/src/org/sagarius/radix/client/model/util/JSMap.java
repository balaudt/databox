package org.sagarius.radix.client.model.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;

public class JSMap<K, V> extends JavaScriptObject {
	protected JSMap() {

	}

	public static native <K, V> JSMap<K, V> create(Class<K> keyClass,
			Class<V> valueClass)/*-{
		return {};
	}-*/;

	public static <K, V> JSMap<K, V> create(Map<K, V> inputMap,
			Class<K> keyClass, Class<V> valueClass) {
		JSMap<K, V> jsMap = create(keyClass, valueClass);
		Set<Entry<K, V>> entrySet = inputMap.entrySet();
		for (Entry<K, V> entry : entrySet) {
			jsMap.put(entry.getKey(), entry.getValue());
		}
		return jsMap;
	}

	public final native JSMap<K, V> put(K key, V value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native V get(String key)/*-{
		return this[key];
	}-*/;

	public final native String render()/*-{
		return JSON.stringify(this);
	}-*/;

	public static native <K, V> JSMap<K, V> create(String json,
			Class<K> keyClass, Class<V> valueClass)/*-{
		return eval(json);
	}-*/;

	private final native BaseEntry getNativeKeys()/*-{
		var result=[];
		var i=0;
		for(var k in this){
			result[i++]=k;
		}
		return result;
	}-*/;

	public final List<K> getKeys(Class<K> keyClass) {
		BaseEntry entry = getNativeKeys();
		int length = entry.length();
		ArrayList<K> result = new ArrayList<K>();
		for (int i = 0; i < length; i++) {
			result.add(entry.get(i, keyClass));
		}
		return result;
	}
}
