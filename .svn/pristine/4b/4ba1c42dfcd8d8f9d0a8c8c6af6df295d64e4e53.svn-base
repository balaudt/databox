package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.gdata.client.Entry;

public class CellEntry extends Entry<CellEntry> {
	protected CellEntry() {
	}

	public static native CellEntry newInstance()/*-{
		return new $wnd.google.gdata.Entry();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Entry;
	}-*/;

	public native final String getNativeCol()/*-{
		return this['gs$cell']['col'];
	}-*/;

	public final int getCol() {
		return Integer.parseInt(getNativeCol());
	}

	public native final String getNativeRow()/*-{
		return this['gs$cell']['row'];
	}-*/;

	public final int getRow() {
		return Integer.parseInt(getNativeRow());
	}

	private native final String getNativeValue()/*-{
		return this['gs$cell']['$t'];
	}-*/;

	public final String getValue() {
		return getNativeValue();
	}

	public final double getDoubleValue() {
		return Double.parseDouble(getNativeValue());
	}

	public final native void setValue(String value)/*-{
		this['gs$cell']['$t']=value;
	}-*/;

	public final int getIntValue() {
		return Integer.parseInt(getNativeValue());
	}
}
