package org.sagarius.radix.server.model.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.sagarius.radix.server.model.BaseEntity;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntryCollection {
	private JSONArray mainElement;
	private String etag;

	public EntryCollection() {
		mainElement = new JSONArray();
	}

	private EntryCollection(JSONArray mainElement) {
		this.mainElement = mainElement;
	}

	public void add(int index, BaseEntry element) {
		mainElement.add(index, element);
	}

	public boolean add(BaseEntry e) {
		return mainElement.add(e);
	}

	public boolean add(BaseEntity e) {
		return mainElement.add(e);
	}

	public boolean addAll(Collection<BaseEntry> c) {
		return mainElement.addAll(c);
	}

	public boolean addAll(int index, Collection<BaseEntry> c) {
		return mainElement.addAll(index, c);
	}

	public BaseEntry get(int index) {
		return (BaseEntry) mainElement.get(index);
	}

	public int indexOf(BaseEntry o) {
		return mainElement.indexOf(o);
	}

	public boolean isEmpty() {
		return mainElement.isEmpty();
	}

	public Iterator iterator() {
		return mainElement.iterator();
	}

	public int lastIndexOf(BaseEntry o) {
		return mainElement.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return mainElement.listIterator();
	}

	public ListIterator listIterator(int index) {
		return mainElement.listIterator(index);
	}

	public BaseEntry remove(int index) {
		return (BaseEntry) mainElement.remove(index);
	}

	public boolean remove(BaseEntry o) {
		return mainElement.remove(o);
	}

	public boolean removeAll(Collection<BaseEntry> c) {
		return mainElement.removeAll(c);
	}

	public Object set(int index, BaseEntry element) {
		return mainElement.set(index, element);
	}

	public int size() {
		return mainElement.size();
	}

	public List subList(int fromIndex, int toIndex) {
		return mainElement.subList(fromIndex, toIndex);
	}

	public BaseEntry[] toArray() {
		return (BaseEntry[]) mainElement.toArray();
	}

	public BaseEntry[] toArray(BaseEntry[] a) {
		return (BaseEntry[]) mainElement.toArray(a);
	}

	public String render() {
		return mainElement.toString();
	}

	public static EntryCollection instantiate(String json) {
		JSONArray theArray = (JSONArray) JSONValue.parse(json);
		JSONArray result = new JSONArray();
		for (Object object : theArray) {
			JSONArray singleElement = (JSONArray) object;
			result.add(new BaseEntry(singleElement));
		}
		return new EntryCollection(result);
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getEtag() {
		return etag;
	}

	@Override
	public String toString() {
		return etag + "\t" + render();
	}
}
