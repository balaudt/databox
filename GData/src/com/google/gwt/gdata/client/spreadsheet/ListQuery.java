package com.google.gwt.gdata.client.spreadsheet;

public class ListQuery extends GDataQuery {
	protected ListQuery() {
	}

	public static native ListQuery newInstance(String feedUri)/*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

	public final void setSpreadsheetQuery(String query) {
		setStringParam("sq", query);
	}

	public final String getSpreadsheetQuery() {
		return getStringParam("sq");
	}

	public final void setOrderBy(String orderby) {
		setStringParam("orderby", orderby);
	}

	public final String getOrderBy() {
		return getStringParam("orderby");
	}

	public final void setReverse(boolean reverse) {
		if (reverse) {
			setStringParam("reverse", "true");
		} else {
			setStringParam("reverse", "false");
		}
	}

	public final boolean isReverse() {
		return Boolean.parseBoolean(getStringParam("reverse"));
	}
}
