package com.sagarius.goddess.client.widgets;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class StructuredWidget extends BaseWidget {

	@Override
	protected void initialize(AsyncCallback<Void> callback) {

	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {

	}

	@Override
	public void refresh() {
		isLoaded = true;
		contentsPanel.clear();
		setLoading(true);
		responseReceived(null);
		setLoading(false);
	}
}
