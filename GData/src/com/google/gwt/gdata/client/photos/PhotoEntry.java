package com.google.gwt.gdata.client.photos;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gdata.client.Entry;

public class PhotoEntry extends Entry<PhotoEntry> {
	protected PhotoEntry() {
	}

	public static native PhotoEntry newInstance()/*-{
		return new $wnd.google.gdata.Entry();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Entry;
	}-*/;

	public final native String getImageUrl()/*-{
		return this['content']['src'];
	}-*/;
}
