package com.sagarius.goddess.client.utils;

import java.io.IOException;
import java.util.LinkedList;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.data.Reference;
import org.restlet.client.data.Status;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public abstract class Pager {
	protected LinkedList<String> cursors = new LinkedList<String>();
	protected int pageNumber = 0;
	protected boolean hasPrevious = false;
	protected boolean hasNext = true;
	private String uri;

	public void refresh() {
		cursors.clear();
		cursors.add(null);
		pageNumber = 0;
		hasNext = true;
		go();
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public boolean hasPrevious() {
		return hasPrevious;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Pager(String uri) {
		cursors.add(null);
		this.uri = uri;
	}

	public void previous() {
		if (!hasPrevious) {
			return;
		}
		pageNumber -= 2;
		go();
	}

	public void next() {
		if (!hasNext) {
			return;
		}
		go();
	}

	public void go() {
		final Reference ref = new Reference(uri);
		if (cursors.get(pageNumber) != null) {
			ref.addQueryParameter("cursor", cursors.get(pageNumber));
		}
		Singletons.getClient().handle(new Request(Method.GET, ref),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						try {
							String json = response.getEntity().getText();
							if (json == null || json.trim().equals("")) {
								return;
							}
							JSONObject object = (JSONObject) JSONParser
									.parse(json);
							if (object.keySet().isEmpty()) {
								emptySet();
								return;
							}
							String cursor = ((JSONString) object.get("cursor"))
									.stringValue();
							Reference headRef = new Reference(uri)
									.addQueryParameter("cursor", cursor);
							Singletons.getClient().handle(
									new Request(Method.HEAD, headRef),
									new Uniform() {

										@Override
										public void handle(Request request,
												Response response) {
											if (response.getStatus().equals(
													Status.SUCCESS_OK)) {
												hasNext = true;
											} else {
												if (pageNumber == 1) {
													singlePage();
												}
												hasNext = false;
											}
										}
									});
							if (pageNumber <= 0) {
								hasPrevious = false;
							} else {
								hasPrevious = true;
							}
							pageNumber++;
							try {
								cursors.set(pageNumber, cursor);
							} catch (IndexOutOfBoundsException e) {
								cursors.add(cursor);
							}
							Pager.this.handle(object.get(getResourceName()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
	}

	protected abstract void emptySet();

	protected abstract void handle(JSONValue value);

	protected abstract String getResourceName();

	protected abstract void singlePage();
}
