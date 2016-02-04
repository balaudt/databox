package com.google.gwt.gdata.client.spreadsheet;

public class DocumentQuery extends GDataQuery {

	protected DocumentQuery() {

	}

	public static native DocumentQuery newInstance(String feedUri) /*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

	public final void setTitleQuery(String titleQuery) {
		setStringParam("title", titleQuery);
	}

	public final String getTitleQuery() {
		return getStringParam("title");
	}

	public final void setTitleExact(boolean titleExact) {
		if (titleExact) {
			setStringParam("title-exact", "true");
		} else {
			setStringParam("title-exact", "false");
		}
	}

	public final boolean isTitleExact() {
		return Boolean.parseBoolean(getStringParam("title-exact"));
	}

	public final void setOrderBy(String orderBy) {
		setStringParam("orderby", orderBy);

	}

	public final String getOrderBy() {
		return getStringParam("orderby");
	}
}
