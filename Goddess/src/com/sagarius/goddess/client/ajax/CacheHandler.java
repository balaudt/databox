package com.sagarius.goddess.client.ajax;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CacheHandler<I, O> {
	void handle(I input, AsyncCallback<O> output);
}
