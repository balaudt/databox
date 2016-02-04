package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gdata.client.Entry;

public class SpreadsheetEntry extends Entry<SpreadsheetEntry> {
	protected SpreadsheetEntry() {
	}

	public static native JavaScriptObject getConstructor() /*-{
		return new $wnd.google.gdata.Entry;
	}-*/;

	public static native SpreadsheetEntry newInstance() /*-{
		return new $wnd.google.gdata.Entry();
	}-*/;

	public final String getKey() {
		String[] paths = getId().getValue().split("");
		return paths[paths.length - 1];
	}
}
