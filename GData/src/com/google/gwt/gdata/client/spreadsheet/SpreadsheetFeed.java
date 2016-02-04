package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;

public class SpreadsheetFeed extends
		GDataFeed<SpreadsheetFeed, SpreadsheetEntry> {
	protected SpreadsheetFeed() {
	}

	@SuppressWarnings("unchecked")
	public static native SpreadsheetFeed newInstance() /*-{
		return new $wnd.google.gdata.Feed();
	}-*/;

	public static final native JavaScriptObject getConstructor() /*-{
		return $wnd.google.gdata.Feed;
	}-*/;

}
