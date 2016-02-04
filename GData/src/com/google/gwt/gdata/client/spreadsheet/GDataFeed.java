package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.gdata.client.Entry;
import com.google.gwt.gdata.client.Feed;

@SuppressWarnings("rawtypes")
public class GDataFeed<F extends Feed, E extends Entry> extends Feed<F, E> {
	protected GDataFeed() {
	}

	public final native String getLastUpdated()/*-{
		return this['updated']['$t'];
	}-*/;
}
