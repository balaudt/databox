package com.sagarius.goddess.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sagarius.goddess.client.login.LoginPage;

public class LoginEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		String nextUrl = Window.Location.getParameter("nextUrl");
		nextUrl = URL.encode(nextUrl);
		RootPanel.get("container").add(new LoginPage(nextUrl));
	}

}
