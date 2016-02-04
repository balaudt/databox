package org.sagarius.radix.client.util;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class JSMap extends JavaScriptObject {
	protected JSMap() {
	}

	public static native JSMap create()/*-{
		return {};
	}-*/;

	public final native JSMap put(String key, String value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native JSMap put(String key, int value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native JSMap put(String key, float value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native JSMap put(String key, double value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native JSMap put(String key, boolean value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native JSMap put(String key, JavaScriptObject value)/*-{
		this[key]=value;
		return this;
	}-*/;

	public final native String getString(String key)/*-{
		return this[key];
	}-*/;

	public final native int getInt(String key)/*-{
		return this[key];
	}-*/;

	public final native float getFloat(String key)/*-{
		return this[key];
	}-*/;

	public final native double getDouble(String key)/*-{
		return this[key];
	}-*/;

	public final native boolean getBoolean(String key)/*-{
		return this[key];
	}-*/;

	public final native JavaScriptObject getObject(String key)/*-{
		return this[key];
	}-*/;

	public final native JsArrayString keySet()/*-{
		var result=[];
		var i=0;
		for(prop in this){
			result[i++]=prop;
		}
		return result;
	}-*/;
}
