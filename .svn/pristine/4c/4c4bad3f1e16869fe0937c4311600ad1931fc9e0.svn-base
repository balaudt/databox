package com.google.gwt.gdata.client.spreadsheet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.gdata.client.Entry;

public class ListEntry extends Entry<ListEntry> {
	protected ListEntry() {
	}

	public static native ListEntry newInstance()/*-{
		return new $wnd.google.gdata.Entry();
	}-*/;

	public static final native JavaScriptObject getConstructor()/*-{
		return $wnd.google.gdata.Entry;
	}-*/;

	public final List<String> getTags() {
		LinkedList<String> tags = new LinkedList<String>();
		JsArrayString nativeTags = getNativeTags();
		int length = nativeTags.length();
		for (int i = 0; i < length; i++) {
			tags.add(nativeTags.get(i));
		}
		return tags;
	}

	public final native String getValue(String tag)/*-{
		return this['gsx$'+tag]['$t'];
	}-*/;

	private final native JsArrayString getNativeTags()/*-{
		var result=[];
		var i=0;
		for(prop in this){
			if(prop.indexOf('gsx$')!=-1){
				result[i++]=prop.substring(4);
			}
		}
		return result;
	}-*/;

	public final Map<String, String> getValues() {
		String text = getContent().getText();
		String[] pairs = text.split(",");
		Map<String, String> result = new HashMap<String, String>();
		for (String pair : pairs) {
			String[] keyValue = pair.split(":");
			result.put(keyValue[0], keyValue[1]);
		}
		return result;
	}
}
