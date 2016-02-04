package com.google.gwt.gdata.server.spreadsheet;

import java.net.MalformedURLException;
import java.net.URL;

public class TestClazz {
	public static void main(String[] args) throws MalformedURLException {
		URL url = new URL(new URL("http://apache.org"), "/coccon");
		System.out.println(url);
	}
}
