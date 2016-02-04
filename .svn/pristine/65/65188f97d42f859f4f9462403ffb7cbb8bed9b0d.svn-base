package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.gdata.client.GDataRequestParameters;
import com.google.gwt.gdata.client.GoogleService;

public class SpreadsheetService extends GoogleService {
	protected SpreadsheetService() {
	}

	public static native SpreadsheetService newInstance(String applicationName)/*-{
		return new $wnd.google.gdata.client.GoogleService(applicationName);
	}-*/;

	public final void getSpreadsheetFeed(String uri,
			SpreadsheetFeedCallback callback) {
		this.getFeed(uri, callback, SpreadsheetFeed.getConstructor(), true,
				null);
	}

	public final void getSpreadsheetFeed(String uri,
			SpreadsheetFeedCallback callback, GDataRequestParameters parameters) {
		this.getFeed(uri, callback, SpreadsheetFeed.getConstructor(), true,
				parameters);
	}

	public final void getSpreadsheetFeed(SpreadsheetQuery query,
			SpreadsheetFeedCallback callback) {
		this.getFeed(query, callback, SpreadsheetFeed.getConstructor(), true,
				null);
	}

	public final void getSpreadsheetFeed(SpreadsheetQuery query,
			SpreadsheetFeedCallback callback, GDataRequestParameters parameters) {
		this.getFeed(query, callback, SpreadsheetFeed.getConstructor(), true,
				parameters);
	}

	public final void getWorksheetFeed(String uri,
			WorksheetFeedCallback callback) {
		this.getFeed(uri, callback, WorksheetFeed.getConstructor(), true, null);
	}

	public final void getWorksheetFeed(String uri,
			WorksheetFeedCallback callback, GDataRequestParameters parameters) {
		this.getFeed(uri, callback, WorksheetFeed.getConstructor(), true,
				parameters);
	}

	public final void getWorksheetFeed(WorksheetQuery query,
			WorksheetFeedCallback callback) {
		this.getFeed(query, callback, WorksheetFeed.getConstructor(), true,
				null);
	}

	public final void getWorksheetFeed(WorksheetQuery query,
			WorksheetFeedCallback callback, GDataRequestParameters parameters) {
		this.getFeed(query, callback, WorksheetFeed.getConstructor(), true,
				parameters);
	}

	public final void getListFeed(String uri, ListFeedCallback callback) {
		this.getFeed(uri, callback, WorksheetFeed.getConstructor(), true, null);
	}

	public final void getListFeed(String uri, ListFeedCallback callback,
			GDataRequestParameters parameters) {
		this.getFeed(uri, callback, ListFeed.getConstructor(), true, parameters);
	}

	public final void getListFeed(ListQuery query, ListFeedCallback callback) {
		this.getFeed(query, callback, WorksheetFeed.getConstructor(), true,
				null);
	}

	public final void getListFeed(ListQuery query, ListFeedCallback callback,
			GDataRequestParameters parameters) {
		this.getFeed(query, callback, ListFeed.getConstructor(), true,
				parameters);
	}

	public final void getCellFeed(String uri, CellFeedCallback callback) {
		this.getFeed(uri, callback, WorksheetFeed.getConstructor(), true, null);
	}

	public final void getCellFeed(String uri, CellFeedCallback callback,
			GDataRequestParameters parameters) {
		this.getFeed(uri, callback, WorksheetFeed.getConstructor(), true,
				parameters);
	}

	public final void getCellFeed(CellQuery query, CellFeedCallback callback) {
		this.getFeed(query, callback, WorksheetFeed.getConstructor(), true,
				null);
	}

	public final void getCellFeed(CellQuery query, CellFeedCallback callback,
			GDataRequestParameters parameters) {
		this.getFeed(query, callback, WorksheetFeed.getConstructor(), true,
				parameters);
	}
}
