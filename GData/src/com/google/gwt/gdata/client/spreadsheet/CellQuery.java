package com.google.gwt.gdata.client.spreadsheet;

public class CellQuery extends GDataQuery {
	protected CellQuery() {
	}

	public static native CellQuery newInstance(String feedUri)/*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

	public final void setMaximumRow(int value) {
		setNumericParam("max-row", value);
	}

	public final int getMaximumRow() {
		return (int) getNumericParam("max-row");
	}

	public final void setMinimumCol(int value) {
		setNumericParam("min-col", value);
	}

	public final int getMinimumCol() {
		return (int) getNumericParam("min-col");
	}

	public final void setMaximumCol(int value) {
		setNumericParam("max-col", value);
	}

	public final int getMaximumCol() {
		return (int) getNumericParam("max-col");
	}

	public final void setMinimumRow(int value) {
		setNumericParam("min-row", value);
	}

	public final int getMinimumRow() {
		return (int) getNumericParam("min-row");
	}

	public final void setRange(String range) {
		setStringParam("range", range);
	}

	public final String getRange() {
		return getStringParam("range");
	}

	public final void setReturnEmpty(boolean returnEmpty) {
		if (returnEmpty) {
			setStringParam("return-empty", "true");
		} else {
			setStringParam("return-empty", "false");
		}
	}

	public final boolean getReturnEmpty() {
		return Boolean.parseBoolean(getStringParam("return-empty"));
	}
}
