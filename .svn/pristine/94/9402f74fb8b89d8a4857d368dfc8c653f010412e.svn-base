package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;

public class CellFeed extends GDataFeed<CellFeed, CellEntry> {
	protected CellFeed() {
	}

	@SuppressWarnings("unchecked")
	public static native CellFeed newInstance()/*-{
		return new $wnd.google.gdata.Feed();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Feed;
	}-*/;

	private final native String getNativeRowCount()/*-{
		return this['gs$rowCount']['$t'];
	}-*/;

	private final native String getNativeColCount()/*-{
		return this['gs$colCount']['$t'];
	}-*/;

	public final int getRowCount() {
		return Integer.parseInt(getNativeRowCount());
	}

	public final int getColCount() {
		return Integer.parseInt(getNativeColCount());
	}
}
