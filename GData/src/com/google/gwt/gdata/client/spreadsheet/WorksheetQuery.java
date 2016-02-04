package com.google.gwt.gdata.client.spreadsheet;

public class WorksheetQuery extends DocumentQuery {
	protected WorksheetQuery() {
	}

	public static native WorksheetQuery newInstance(String feedUri)/*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

}
