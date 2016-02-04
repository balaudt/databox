package org.sagarius.radix.server.model.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONValue;

public class BaseEntry implements JSONAware {
	private JSONArray mainElement;
	private String etag;

	public BaseEntry() {
		mainElement = new JSONArray();
	}

	BaseEntry(JSONArray mainElement) {
		this.mainElement = mainElement;
	}

	@SuppressWarnings("unchecked")
	public void set(int index, Object object) {
		mainElement.ensureCapacity(index);
		int size = mainElement.size();
		if (size <= index) {
			for (int i = size; i <= index; i++) {
				mainElement.add(null);
			}
		}
		mainElement.set(index, object);
	}

	public Object get(int index) {
		try {
			return mainElement.get(index);
		} catch (Exception e) {
			return null;
		}
	}

	public String render() {
		return mainElement.toString();
	}

	public static BaseEntry instantiate(String json) {
		return new BaseEntry((JSONArray) JSONValue.parse(json));
	}

	@Override
	public String toJSONString() {
		return mainElement.toString();
	}

	@Override
	public String toString() {
		return etag + "\t" + mainElement.toString();
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getEtag() {
		return etag;
	}
}
