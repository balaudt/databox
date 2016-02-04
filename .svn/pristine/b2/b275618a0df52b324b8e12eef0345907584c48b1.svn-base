package com.sagarius.goddess.client.gadgets;

import org.restlet.client.Response;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.Query.Options;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;

public abstract class VisualizationGadget extends BaseGadget {

	private String[] loadedPackages;

	public VisualizationGadget(String clazz, String requestUri,
			String... packages) {
		super(clazz, requestUri);
		this.loadedPackages = packages;
	}

	@Override
	public void responseReceived(Response response) {
	}

	protected Options options = Options.create();

	@Override
	public void refresh() {
		setLoading(true);
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				Query query = Query.create(requestUri, options);
				query.send(new Callback() {

					@Override
					public void onResponse(QueryResponse response) {
						if (response.isError()) {
							statusMessage.setHTML(response.getDetailedMessage());
							setLoading(false);
							return;
						}
						contentsPanel.clear();
						DataTable table = response.getDataTable();
						if (table.getNumberOfRows() == 0) {
							Label noLabel = new Label("No data");
							noLabel.setStyleName("clear");
							contentsPanel.add(noLabel);
							setLoading(false);
							statusMessage.setText("");
							return;
						}
						responseRecevied(response);
						setLoading(false);
					}
				});
			}
		};
		VisualizationUtils.loadVisualizationApi(runnable, loadedPackages);
	}

	public abstract void responseRecevied(QueryResponse response);
}
