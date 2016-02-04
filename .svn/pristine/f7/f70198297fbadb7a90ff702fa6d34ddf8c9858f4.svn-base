package org.sagarius.radix.client.model.util;

import com.google.gwt.core.client.JavaScriptObject;

public class BaseEntry extends JavaScriptObject {
	protected BaseEntry() {
	}

	public static native BaseEntry create()/*-{
		return [];
	}-*/;

	public static native BaseEntry create(String json)/*-{
		return eval(json);
	}-*/;

	public final void set(int index, Object object) {
		if (object instanceof Integer) {
			set(index, ((Integer) object).intValue());
		} else if (object instanceof String) {
			set(index, (String) object);
		} else if (object instanceof Float) {
			set(index, ((Float) object).floatValue());
		} else if (object instanceof Double) {
			set(index, ((Double) object).doubleValue());
		} else if (object instanceof Boolean) {
			set(index, ((Boolean) object).booleanValue());
		} else if (object instanceof JavaScriptObject) {
			set(index, (JavaScriptObject) object);
		}
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(int index, Class<T> type) {
		if (isNull(index)) {
			return null;
		}
		if (type.equals(String.class)) {
			return (T) getString(index);
		} else if (type.equals(Boolean.class)) {
			return (T) Boolean.valueOf(getBoolean(index));
		} else if (type.equals(Integer.class)) {
			return (T) (Integer) getInteger(index);
		} else if (type.equals(Float.class)) {
			return (T) (Float) getFloat(index);
		} else if (type.equals(Double.class)) {
			return (T) (Double) getDouble(index);
		} else {
			return (T) getObject(index);
		}
	}

	private final native boolean isNull(int index)/*-{
		var i=this[index];
		if(i){
			return false;
		}else{
			return true;
		}
	}-*/;

	private final native int getInteger(int index) /*-{
		return this[index];
	}-*/;

	private final native float getFloat(int index) /*-{
		return this[index];
	}-*/;

	private final native double getDouble(int index) /*-{
		return this[index];
	}-*/;

	private final native boolean getBoolean(int index) /*-{
		return this[index];
	}-*/;

	private final native String getString(int index) /*-{
		return this[index];
	}-*/;

	private native final <T extends JavaScriptObject> T getObject(int index) /*-{
		return this[index];
	}-*/;

	private native final void set(int index, double doubleValue) /*-{
		this[index]=doubleValue;
	}-*/;

	private native final void set(int index, float floatValue) /*-{
		this[index]=floatValue;
	}-*/;

	private native final void set(int index, JavaScriptObject object) /*-{
		this[index]=object;
	}-*/;

	private native final void set(int index, boolean booleanValue) /*-{
		this[index]=booleanValue;
	}-*/;

	private native final void set(int index, String strValue) /*-{
		this[index]=strValue;
	}-*/;

	private native final void set(int index, int intValue) /*-{
		this[index]=intValue;
	}-*/;

	public native final String render()/*-{
		return JSON.stringify(this);
	}-*/;

	public native final int length()/*-{
		return this.length;
	}-*/;
}
