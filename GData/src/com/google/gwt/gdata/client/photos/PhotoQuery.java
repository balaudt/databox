package com.google.gwt.gdata.client.photos;

import com.google.gwt.gdata.client.spreadsheet.GDataQuery;

public class PhotoQuery extends GDataQuery {
	protected PhotoQuery() {
	}

	public static native PhotoQuery newInstance(String feedUri)/*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

	public final String getImgMax() {
		return getStringParam("imgmax");
	}

	public final void setImgMax(String imgmax) {
		setStringParam("imgmax", imgmax);
	}

	public final String getTag() {
		return getStringParam("tag");
	}

	public final void setTag(String tag) {
		setStringParam("tag", tag);
	}

	public final String getKind() {
		return getStringParam("kind");
	}

	public final void setKind(String kind) {
		setStringParam("kind", kind);
	}

	public final String getThumbSize() {
		return getStringParam("thumbsize");
	}

	public final void setThumbSize(String thumbsize) {
		setStringParam("thumbsize", thumbsize);
	}

}
