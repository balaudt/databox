package com.sagarius.goddess.client.gadgets;

import org.restlet.client.Response;

import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.ListFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.ListQuery;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gwt.user.client.ui.Label;

public abstract class ListDataGadget extends BaseGadget implements
		ListFeedCallback {
	// protected String lastUpdated;
	protected ListQuery query;
	protected String uri;
	protected SpreadsheetService service;
	protected FeedURLFactory factory;
	protected boolean isQuery;

	@Override
	public void onFailure(CallErrorException caught) {
		// if (caught.getError().getStatusTextContentType().equals("304")) {
		// setLoading(false);
		// return;
		// }
		statusMessage.setHTML(caught.getMessage());
		setLoading(false);
		return;
	}

	@Override
	public void onSuccess(ListFeed result) {
		// String updated = result.getLastUpdated();
		// if (lastUpdated != null && lastUpdated.equals(updated)) {
		// setLoading(false);
		// return;
		// }
		// lastUpdated = updated;
		contentsPanel.clear();
		if (result.getTotalResults().getValue() == 0) {
			Label noLabel = new Label("No data");
			noLabel.setStyleName("clear");
			contentsPanel.add(noLabel);
			setLoading(false);
			return;
		}
		responseReceived(result);
		setLoading(false);
	}

	public ListDataGadget(String clazz, String requestUri) {
		super(clazz, requestUri);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				service = SpreadsheetService.newInstance("Goddess");
				factory = FeedURLFactory.getDefault();
			}
		};
		GData.loadGDataApi(null, runnable);
	}

	public abstract void responseReceived(ListFeed feed);

	@Override
	public void refresh() {
		setLoading(true);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// if (!isQuery) {
				// query = ListQuery.newInstance(uri);
				// }
				// if (lastUpdated != null) {
				// query.setUpdatedMin(lastUpdated);
				// }

				if (isQuery) {
					service.getListFeed(query, ListDataGadget.this);
				} else {
					service.getListFeed(uri, ListDataGadget.this);
				}
			}
		};
		GData.loadGDataApi(null, runnable);
	}

	@Override
	public void responseReceived(Response response) {
	}

}
