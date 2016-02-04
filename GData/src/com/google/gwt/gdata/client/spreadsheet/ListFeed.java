package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;

public class ListFeed extends GDataFeed<ListFeed, ListEntry> {
	protected ListFeed() {
	}

	@SuppressWarnings("unchecked")
	public static native ListFeed newInstance()/*-{
		return new $wnd.google.gdata.Feed();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Feed;
	}-*/;

}
