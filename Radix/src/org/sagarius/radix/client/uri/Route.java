package org.sagarius.radix.client.uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Route {
	private Template template;
	private List<URIListener> listeners;

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public Route(String uriTemplate, URIListener... listeners) {
		template = new Template(uriTemplate);
		this.setListeners(Arrays.asList(listeners));
	}

	public void add(URIListener listener) {
		if (getListeners() == null) {
			setListeners(new ArrayList<URIListener>());
		}
		getListeners().add(listener);
	}

	public void setListeners(List<URIListener> listeners) {
		this.listeners = listeners;
	}

	public List<URIListener> getListeners() {
		return listeners;
	}
}
