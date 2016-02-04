package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;
import com.sagarius.goddess.client.view.util.ControlPanel;
import com.sagarius.goddess.client.view.util.MessagePopup;
import com.sagarius.goddess.client.view.util.NewSMSPopup;
import com.sagarius.goddess.client.view.util.StandardWidget;
import com.sagarius.goddess.client.view.util.StandardsGadget;

public class CategoryClassPresenter extends StandardsGadget {
	private String category;

	public CategoryClassPresenter(String category) {
		this.category = category;
	}

	@Override
	public void addControls() {
		for (final StandardWidget standard : standards) {
			ControlPanel controlPanel = standard.getControlPanel();
			controlPanel.add(new Anchor("Download Name list", "javascript:"),
					"icon-16-inbox-download").add(
					new Anchor("Send SMS", "javascript:"),
					"icon-16-mobile-phone-cast");
			((Anchor) (controlPanel.getControlAt(2)))
					.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							standard.getVisualizationQuery(new AsyncCallback<String>() {

								@Override
								public void onSuccess(String query) {
									if (query != null) {
										Window.open(
												query.concat("&tqx=out:csv"),
												"_blank", "");
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									MessagePopup.INSTANCE
											.setMessage(
													"Download failed.  Please try again later.")
											.center();
								}
							});

						}
					});
			((Anchor) (controlPanel.getControlAt(3)))
					.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							String spreadsheetQuery = standard
									.getSpreadsheetQuery();
							if (spreadsheetQuery != null) {
								String groupsQuery = standard.getGroupsQuery();
								NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
								smsWidget
										.setRequestUri("/sms?g=" + groupsQuery
												+ "&type=PARENT&tq="
												+ spreadsheetQuery);
								smsWidget.setCapabilities(NewSMSPopup.NONE);
								smsWidget.center();
							}
						}
					});
		}

		Anchor downloadAnchor = new Anchor("Download Reports", "javascript:");
		Anchor messageAnchor = new Anchor("Send Message", "javascript:");
		downloadAnchor.setStyleName("icon-16-inbox-download mr4");
		controlsPanel.add(downloadAnchor);
		// controlsPanel.add(new BoxItemWidget(5, "icon-32-chart_bar",
		// "Reports")
		// .addControl(downloadAnchor));
		messageAnchor.setStyleName("icon-16-mobile-phone-cast mr4");
		controlsPanel.add(messageAnchor);
		// controlsPanel.add(new BoxItemWidget(5, "icon-32-phone_sound", "SMS")
		// .addControl(messageAnchor));
		downloadAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getVisualizationQuery(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						MessagePopup.INSTANCE.setMessage(
								"Download failed.  Please try again later.")
								.center();
					}

					@Override
					public void onSuccess(String visualizationQuery) {
						if (visualizationQuery != null) {
							Window.open(
									visualizationQuery.concat("&tqx=out:csv"),
									"_blank", "");
						}
					}
				});

			}
		});
		messageAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String query = getSpreadsheetQuery();
				if (query == null) {
					MessagePopup.INSTANCE.setMessage(
							"Select a minimum of a class to send messages")
							.center();
					return;
				}
				NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
				smsWidget.setCapabilities(NewSMSPopup.NONE);
				smsWidget.setRequestUri("/sms?g=" + getGroupsQuery()
						+ "&type=PARENT&tq=" + query);
				smsWidget.center();
			}
		});
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		builder.removeFilters();
		if (category != null) {
			builder.addFilter(Generic.CATEGORY, Operator.EQ, category);
		}
		String requestUri = builder.get();
		if (request == null) {
			request = new GenericRequest().setType(RequestType.VISUALIZATION);
		}
		request.setVisualizationQuery(requestUri);
		callback.onSuccess(null);
	}
}
