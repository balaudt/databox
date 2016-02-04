package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gdata.client.Entry;

public class WorksheetEntry extends Entry<WorksheetEntry> {
	protected WorksheetEntry() {
	}

	public static native JavaScriptObject getConstructor() /*-{
		return new $wnd.google.gdata.Entry;
	}-*/;

	public static native WorksheetEntry newInstance() /*-{
		return new $wnd.google.gdata.Entry();
	}-*/;

	public final int getRowCount() {
		return Integer.parseInt(getNativeRowCount());
	}

	private final native String getNativeRowCount()/*-{
		return this['gs$rowCount']['$t'];
	}-*/;

	public final int getColCount() {
		return Integer.parseInt(getNativeColCount());
	}

	private final native String getNativeColCount()/*-{
		return this['gs$colCount']['$t'];
	}-*/;

	public final String getListFeedUrl() {
		return getLink("http://schemas.google.com/spreadsheets/2006#listfeed")
				.getHref();
	}

	public final String getCellFeedUrl() {
		return getLink("http://schemas.google.com/spreadsheets/2006#cellsfeed")
				.getHref();
	}

	public final String getVisualizationUrl() {
		return getLink(
				"http://schemas.google.com/visualization/2008#visualizationApi")
				.getHref();
	}
}
