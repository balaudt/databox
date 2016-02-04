package com.sagarius.goddess.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

public class MultipleEntryPoint implements EntryPoint {
	public static final Map<String, EntryPoint> PAGE_MAPPER = new HashMap<String, EntryPoint>();
	static {
		PAGE_MAPPER.put("/login.html", new LoginEntryPoint());
		PAGE_MAPPER.put("/", new IndexEntryPoint());
		PAGE_MAPPER.put("/index.html", new IndexEntryPoint());
		PAGE_MAPPER.put("/register.html", new RegisterEntryPoint());
	}

	@Override
	public void onModuleLoad() {
		String path = Window.Location.getPath();
		EntryPoint point = PAGE_MAPPER.get(path);
		point.onModuleLoad();
	}

}
