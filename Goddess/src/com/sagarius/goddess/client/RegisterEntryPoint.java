package com.sagarius.goddess.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class RegisterEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("container").add(new RegisterPage());
	}

}
