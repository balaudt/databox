package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;

public class WorksheetFeed extends GDataFeed<WorksheetFeed, WorksheetEntry> {
	protected WorksheetFeed() {
	}

	@SuppressWarnings("unchecked")
	public static native WorksheetFeed newInstance()/*-{
		return new $wnd.google.gdata.Feed();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Feed;
	}-*/;

}
