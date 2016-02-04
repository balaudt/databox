package com.sagarius.goddess.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public abstract class LoadWrapper extends Widget {

	public LoadWrapper(Element a) {
		setElement(a);
	}

	public abstract void widgetLoaded();

	@Override
	protected void onLoad() {
		widgetLoaded();
	}
}
