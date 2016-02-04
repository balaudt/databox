package org.sagarius.radix.client.collect;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

public class Taffy extends JavaScriptObject {
	protected Taffy() {

	}

	public static native Taffy create()/*-{
		return new $wnd.TAFFY([]);
	}-*/;

	public static native Taffy create(JsArray<JavaScriptObject> initialObject)/*-{
		return new $wnd.TAFFY(initialObject);
	}-*/;

	public final native void insert(JavaScriptObject object)/*-{
		this.insert(object);
	}-*/;

	public final native void remove(Filter filter)/*-{
		this.remove(filter);
	}-*/;

	public final native JavaScriptObject first()/*-{
		return this.first();
	}-*/;

	public final native JavaScriptObject last()/*-{
		return this.last();
	}-*/;

	public final native JsArrayInteger find(Filter filter)/*-{
		return this.find(filter);
	}-*/;

	public final native JsArray<JavaScriptObject> get(Filter filter)/*-{
		return this.get(filter);
	}-*/;

	public final native JsArray<JavaScriptObject> get(JsArrayInteger i)/*-{
		return this.get(i);
	}-*/;

}
