package com.sagarius.goddess.client.ajax;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class MultiRequestCallback {
	private short status;
	private List<GenericRequest> requests = new LinkedList<GenericRequest>();
	private List<AsyncCallback<GenericResponse>> responseCallbacks = new LinkedList<AsyncCallback<GenericResponse>>();
	private short maxRetryCount = 0;
	private List<Listener> listeners = new LinkedList<Listener>();
	private boolean isRunning;
	private short[] retryCounts = null;
	protected boolean isFailed;

	public abstract void complete();

	public boolean isRunning() {
		return isRunning;
	}

	public MultiRequestCallback add(GenericRequest request,
			AsyncCallback<GenericResponse> response) {
		requests.add(request);
		responseCallbacks.add(response);
		return this;
	}

	public MultiRequestCallback addListener(Listener listener) {
		listeners.add(listener);
		return this;
	}

	public void start() {
		status = 0;
		isRunning = true;
		isFailed = false;
		handle();
	}

	private void handle() {
		int size = requests.size();
		retryCounts = new short[size];
		for (int i = 0; i < size; i++) {
			retryCounts[i] = 0;
			singleRequestHandle(i);
		}
	}

	public void singleRequestHandle(final int i) {
		GenericClient client = new GenericClient();
		GenericRequest request = requests.get(i);
		final AsyncCallback<GenericResponse> callback = responseCallbacks
				.get(i);
		client.handle(request, new AsyncCallback<GenericResponse>() {

			@Override
			public void onSuccess(GenericResponse result) {
				callback.onSuccess(result);
				status++;
				fireEvent();
			}

			@Override
			public void onFailure(Throwable caught) {
				if (retryCounts[i] == maxRetryCount) {
					callback.onFailure(caught);
					isFailed = true;
					status++;
					fireEvent();
					return;
				}
				retryCounts[i]++;
				singleRequestHandle(i);
			}
		});
	}

	private void fireEvent() {
		for (Listener listener : listeners) {
			listener.stateChanged(status);
		}
		if (status == requests.size()) {
			complete();
		}
	}
}
