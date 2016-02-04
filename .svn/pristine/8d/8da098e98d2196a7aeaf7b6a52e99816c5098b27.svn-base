package com.sagarius.goddess.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sagarius.radix.client.model.util.EntryCollection;
import org.sagarius.radix.client.uri.Template;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.photos.PhotoFeed;
import com.google.gwt.gdata.client.photos.PhotoFeedCallback;
import com.google.gwt.gdata.client.photos.PhotoQuery;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.School;
import com.sagarius.goddess.client.utils.Singletons;

public class Utils {
	public static School currentSchool;
	public static String sheetVizUrl = "https://spreadsheets.google.com/tq";
	public static String sheetViewUrl = "https://spreadsheets.google.com/ccc";

	public static boolean isEmpty(String input) {
		if (input == null || input.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static List<Document> getDocuments(String responseString) {
		EntryCollection collection = EntryCollection.create(responseString);
		int size = collection.size();
		LinkedList<Document> results = new LinkedList<Document>();
		for (int i = 0; i < size; i++) {
			results.add(new Document(collection.get(i)));
		}
		return results;
	}

	public static void getImageUrl(final String tag, final String size,
			final AsyncCallback<String> callback) {
		GData.loadGDataApi(null, new Runnable() {

			@Override
			public void run() {
				final PhotoQuery query = PhotoQuery
						.newInstance("http://picasaweb.google.com/data/feed/api/user/default");
				query.setKind("photo");
				query.setTag(tag);
				query.setImgMax(size);
				Singletons.getPicasaService().getPhotoFeed(query,
						new PhotoFeedCallback() {

							@Override
							public void onSuccess(PhotoFeed result) {
								double results = result.getTotalResults()
										.getValue();
								if (results < 1) {
									return;
								}
								callback.onSuccess(result.getEntries()[0]
										.getImageUrl());
							}

							@Override
							public void onFailure(CallErrorException caught) {
								callback.onFailure(caught);
							}
						});
			}
		});

	}

	public static Map<String, String> getAttributes(String uri,
			Template template) {
		String[] paths = uri.split("/");
		Map<Integer, String> attributes = template.getAttributes();
		Map<String, String> result = new HashMap<String, String>();
		Set<Entry<Integer, String>> entrySet = attributes.entrySet();
		for (Entry<Integer, String> entry : entrySet) {
			Integer index = entry.getKey();
			result.put(entry.getValue(), paths[index]);
		}
		return result;
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen)
				.append(Character.toUpperCase(str.charAt(0)))
				.append(str.substring(1)).toString();
	}

	// TODO Changes required
	public static String getString(JSONValue value) {
		if (value == null || value.equals(JSONNull.getInstance())) {
			return "";
		}
		if (value instanceof JSONString) {
			return ((JSONString) value).stringValue();
		}
		if (value instanceof JSONNumber) {
			return ((JSONNumber) value).doubleValue() + "";
		}
		return "";
	}

	public static native void log(JavaScriptObject object)/*-{
		console.log(object);
	}-*/;

	public static void setActiveMainMenu(String linkString) {
		linkString = "#" + linkString;
		Element menubar = DOM.getElementById("menubar");
		NodeList<Node> nodes = menubar.getChildNodes();
		int length = nodes.getLength();
		for (int i = 0; i < length; i++) {
			AnchorElement aNode = (AnchorElement) nodes.getItem(i).getChild(0);
			String innerText = aNode.getHref();
			if (innerText.contains(linkString)) {
				aNode.setClassName("selected");
			} else {
				aNode.removeClassName("selected");
			}
		}
	}

	public static final DateTimeFormat DATE_FORMATTER = DateTimeFormat
			.getFormat("dd-MM-yyyy");
	public static final DateTimeFormat TIME_FORMATTER = DateTimeFormat
			.getFormat("hh:mm a");
}
