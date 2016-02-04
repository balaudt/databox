package org.sagarius.radix.client.model.util;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;

public class EntryCollection extends JavaScriptObject {

	protected EntryCollection() {

	}

	public native static EntryCollection create()/*-{
		return [];
	}-*/;

	public native static EntryCollection create(String json)/*-{
		return eval(json);
	}-*/;

	public final native void add(int index, BaseEntry element)/*-{
		this[index]=element;
	}-*/;

	public final void add(BaseEntry element) {
		int index = size();
		add(index, element);
	}

	public final void addAll(Collection<BaseEntry> c) {
		for (BaseEntry t : c) {
			add(t);
		}
	}

	public final void addAll(int index, Collection<BaseEntry> c) {
		for (BaseEntry t : c) {
			add(index, t);
			index++;
		}
	}

	public final native BaseEntry get(int index)/*-{
		return this[index];
	}-*/;

	public final native boolean isEmpty()/*-{
		return this==[];
	}-*/;

	public final native String render()/*-{
		return JSON.stringify(this);
	}-*/;

	public final native int size()/*-{
		return this.length;
	}-*/;
}
