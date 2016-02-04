package com.sagarius.goddess.client.ajax;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class RequestSynchronizer {
	public static interface RequestGenerator {
		public GenericRequest getRequest(GenericResponse response);
	}

	private GenericRequest initialRequest;
	private List<RequestGenerator> generators = new LinkedList<RequestGenerator>();
	private List<Listener> listeners = new LinkedList<Listener>();
	private AsyncCallback<GenericResponse> terminator = null;
	private short status = 0;
	private GenericClient client = new GenericClient();
	private short[] retryCounts = null;
	private short maxRetryCount = 2;
	private boolean isRunning;

	public boolean isRunning() {
		return isRunning;
	}

	public RequestSynchronizer(GenericRequest initialRequest) {
		this.initialRequest = initialRequest;
	}

	public RequestSynchronizer add(RequestGenerator generator) {
		generators.add(generator);
		return this;
	}

	public RequestSynchronizer setCallback(
			AsyncCallback<GenericResponse> callback) {
		terminator = callback;
		return this;
	}

	public void start() {
		status = 0;
		retryCounts = new short[generators.size() + 1];
		isRunning = true;
		handle(initialRequest);
	}

	private void handle(final GenericRequest request) {
		client.handle(request, new AsyncCallback<GenericResponse>() {

			@Override
			public void onSuccess(GenericResponse result) {
				retryCounts[status] = 0;
				RequestGenerator generator = null;
				try {
					generator = generators.get(status++);
				} catch (IndexOutOfBoundsException e) {
					// It means the queue has ended and the response is sent to
					// callback
					isRunning = false;
					terminator.onSuccess(result);
					return;
				}
				GenericRequest nextRequest = generator.getRequest(result);
				handle(nextRequest);
				for (Listener listener : listeners) {
					listener.stateChanged(status);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				if (retryCounts[status] == maxRetryCount) {
					isRunning = false;
					terminator.onFailure(caught);
					return;
				}
				retryCounts[status]++;
				handle(request);
			}
		});
	}

	public RequestSynchronizer addListener(Listener listener) {
		listeners.add(listener);
		return this;
	}

}
