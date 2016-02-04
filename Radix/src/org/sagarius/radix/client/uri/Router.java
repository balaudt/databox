package org.sagarius.radix.client.uri;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Router {
	private List<Route> routes = new LinkedList<Route>();

	public void attach(String uriTemplate, URIListener listener) {
		routes.add(new Route(uriTemplate, listener));
	}

	public void handle(String uri) {
		for (Route route : routes) {
			if (route.getTemplate().matches(uri)) {
				List<URIListener> listeners = route.getListeners();
				for (URIListener listener : listeners) {
					listener.handle(getAttributes(uri, route.getTemplate()));
				}
			}
		}
	}

	private Map<String, String> getAttributes(String uri, Template template) {
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
}
