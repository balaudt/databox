/*
 * Copyright 2007 Manuel Carrasco Moñino. (manuel_carrasco at users.sourceforge.net) 
 * http://code.google.com/p/gwtchismes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sagarius.radix.client.view;

import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>
 * <b>Just a bunch of utility methods used in the gwtchismes library</b>
 * </p>
 * 
 * @author Manuel Carrasco Moñino
 */
public class GWTCHelper {

	/**
	 * Detection of Internet Explorer 6.x
	 * 
	 * @return true if the browser is ie6
	 */
	public static native boolean isIE6() /*-{
		return (window.XMLHttpRequest)? false: true;
	}-*/;

	/**
	 * Maximize a widget to fill all the document
	 * 
	 * @param widget
	 */
	public static void maximizeWidget(Widget widget) {
		if (widget == null)
			return;
		int w = Math.max(getClientWidth(), Math.max(getWindowScrollWidth(),
				RootPanel.get().getOffsetWidth()));
		int h = Math.max(getClientHeight(), Math.max(getWindowScrollHeight(),
				RootPanel.get().getOffsetHeight()));
		// h = Math.max(h, Window.getClientHeight());
		// h += Window.getScrollTop();
		widget.setSize(w + "px", h + "px");
	}

	private static native int getClientWidth() /*-{
		return $doc.documentElement.clientWidth || $doc.body.clientWidth;
	}-*/;

	private static native int getClientHeight() /*-{
		return $doc.documentElement.clientHeight || $doc.body.clientHeight;
	}-*/;

	private static native int getWindowScrollHeight() /*-{
		try {
		return $doc.compatMode == 'CSS1Compat' ?
		$doc.documentElement.scrollHeight : $doc.body.scrollHeight;
		} catch(e) {
		alert ("getWindowScrollHeight " +  $doc.compatMode + " " + e);
		return 100; 
		}
	}-*/;

	private static native int getWindowScrollWidth() /*-{
		try {
		return $doc.compatMode == 'CSS1Compat' ?
		$doc.documentElement.scrollWidth : $doc.body.scrollWidth;
		} catch(e) {
		alert ("getWindowScrollWidth " +  $doc.compatMode + " " + e);
		return 100; 
		}
	}-*/;

	/**
	 * Changes curly bracket paramters with the correct values
	 * 
	 * @param s
	 *            string to iternationalize, i.e. "Page {1} of {2}"
	 * @param os
	 *            objects, i.e. [2,5]
	 * @return internationalized string. i.e. "Page 2 of 5"
	 */
	public static String internationalize(String s, Object[] os) {
		for (int i = 0; i < os.length; i++) {
			String o = "" + (os[i] != null ? os[i] : "");
			String c = "{" + i + "}";
			for (;;) {
				int pos = s.indexOf(c);
				if (pos < 0)
					break;
				String trail = "";
				if (pos + c.length() < s.length())
					trail = s.substring(pos + c.length());
				s = s.substring(0, pos) + o + trail;
			}
		}
		return s;
	}

	public static String internationalize(String s, String o) {
		Object[] os = { o };
		return internationalize(s, os);
	}

	/**
	 * Returns true if the application is running in GWT hosted mode
	 */
	public static boolean isHostedMode() {
		return !GWT.isScript();
	}

	/**
	 * Returns the location url of the window
	 */
	public static String getLocationUrl() {
		return Window.Location.getHref();
	}

	public static native Element getHeadElement() /*-{
		return ($doc.head);
	}-*/;

	/**
	 * Looks for a javascript tag placed in the page which have a known id and
	 * returns the location
	 * 
	 * @param id
	 *            the id of the script tag
	 * @return the source of the script or null if not found
	 */
	public static String getScriptLocation(String id) {
		Element js = DOM.getElementById(id);
		if (js == null)
			return "";
		String url = DOM.getElementAttribute(js, "src");
		if (url == null)
			return "";
		return url;
	}

	/**
	 * Calculates the location of a web resource that is located in the same
	 * path of other whose location it's known
	 * 
	 * @param url
	 *            location of a known resource
	 * @param asset
	 *            name of the asset
	 * @return full path for the asset
	 */
	public static String calculateLocation(String url, String asset) {
		return (url.contains("://") ? url.replaceFirst("[^/][^/]+$", "") : "")
				+ asset;
	}

	/**
	 * Loads a css resource including it in the head block
	 * 
	 * @param url
	 *            full path of the css resource
	 */
	public static void insertCSS(String url) {
		if (DOM.getElementById(url) != null)
			return;
		Element head = null;
		Element body = RootPanel.getBodyElement();
		Element doc = DOM.getParent(body);
		for (int i = 0; i < DOM.getChildCount(doc); i++) {
			Element child = DOM.getChild(doc, i);
			if (child.toString().toLowerCase().contains("head")) {
				head = child;
				break;
			}
		}
		if (head == null) {
			head = DOM.createElement("head");
			DOM.appendChild(doc, head);
		}
		Element css = DOM.createElement("link");
		DOM.setElementAttribute(css, "rel", "stylesheet");
		DOM.setElementAttribute(css, "type", "text/css");
		DOM.setElementAttribute(css, "href", url);
		DOM.setElementAttribute(css, "id", url);
		DOM.appendChild(head, css);
	}

	/**
	 * Loads a script into the page
	 * 
	 * @param id
	 *            id to give to the new element
	 * @param url
	 *            path for the script
	 */
	public static void insertJS(String id, String url) {
		Element js = DOM.getElementById(id);
		Element parent = RootPanel.getBodyElement();
		if (js != null) {
			parent = DOM.getParent(js);
			DOM.removeChild(parent, js);
		}
		js = DOM.createElement("script");
		DOM.appendChild(parent, js);
		DOM.setElementAttribute(js, "src", url);
		DOM.setElementAttribute(js, "type", "text/javascript");
		DOM.setElementAttribute(js, "id", id != null ? id : url);
	}

	/**
	 * Returns the first part of an url (protocol and hostname)
	 * 
	 * @param url
	 *            reference url
	 * @return protocol and hostname, or original url
	 */
	public static String getProtocolAndHostFromUrl(String url) {
		return url.replaceAll("([^/])[/?][^/].*$", "$1");
	}

	/**
	 * Returns the protocol part of an url (http or https)
	 * 
	 * @param url
	 *            reference url
	 * @return protocol
	 */
	public static String getProtocolFromUrl(String url) {
		return url.toLowerCase().matches("^http") ? url.toLowerCase()
				.replaceFirst("^(https*)", "$1") : "http";
	}

	/**
	 * Returns the hostname of an url
	 * 
	 * @param url
	 *            reference url
	 * @return hostname
	 */
	public static String getHostFromUrl(String url) {
		return url.toLowerCase().replaceFirst("^https*\\://", "")
				.replaceAll("[\\?:/].*$", "");
	}

	/**
	 * Returns the first path element of an url. Normally it is the servlet path
	 * in a java application
	 * 
	 * @param url
	 *            reference url
	 * @return first word of the path
	 */
	public static String getSrvltContPathFromUrl(String url) {
		return url.replaceFirst("^https*\\://[^/]+/+", "").replaceAll(
				"[\\?:/].*$", "");
	}

	/**
	 * Return the value of a parameter present in the url
	 * 
	 * @param url
	 *            reference url
	 * @param key
	 *            parameter name
	 * @return the value or null if it is not present
	 */
	public static String getParameterFromUrl(String url, String key) {
		String reg = "[?&]" + key + "=";
		url = url.replaceAll("&amp;", "&");
		return url.matches(".*" + reg + ".*") ? url.replaceFirst(
				"^.*[?&]" + key + "=", "").replaceAll("&.*$", "") : null;
	}

	/**
	 * Browse an element tree looking for the first element that matches
	 * attribute=value
	 * 
	 * @param element
	 * @param attribute
	 * @param value
	 * @return the first matched element
	 */
	public static Element getFirstElementByAttr(Element element,
			String attribute, String value) {
		int n = DOM.getChildCount(element);
		for (int i = 0; i < n; i++) {
			Element e = DOM.getChild(element, i);
			String v = DOM.getElementAttribute(e, attribute);
			if (v != null && v.matches(value))
				return e;
			if (DOM.getChildCount(element) > 0)
				e = getFirstElementByAttr(e, attribute, value);
			if (e != null)
				return e;
		}
		return null;
	}

	public static List<Element> findElementsWithClass(Element element,
			String className, List<Element> result) {
		if (result == null) {
			result = new Vector<Element>();
		}

		String cls = DOM.getElementProperty(element, "className");
		if (cls != null && cls.indexOf(className) >= 0) {
			result.add(element);
		}

		int childCount = DOM.getChildCount(element);
		for (int i = 0; i < childCount; i++) {
			findElementsWithClass(DOM.getChild(element, i), className, result);
		}

		return result;
	}

	// public static void insertCrossScript(String url, Class c, String method)
	// {
	// url = url + "?call=" + "@" + c.getName() + "::" + method + "()";
	// insertJS(null, url);
	// }

}
