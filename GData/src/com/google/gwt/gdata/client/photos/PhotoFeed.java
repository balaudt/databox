package com.google.gwt.gdata.client.photos;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gdata.client.Feed;

public class PhotoFeed extends Feed<PhotoFeed, PhotoEntry> {
	protected PhotoFeed() {
	}

	@SuppressWarnings("unchecked")
	public static native PhotoFeed newInstance()/*-{
		return new $wnd.google.gdata.Feed();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Feed;
	}-*/;

}
