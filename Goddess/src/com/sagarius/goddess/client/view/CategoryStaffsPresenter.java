package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;
import com.sagarius.goddess.client.view.util.NewSMSPopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class CategoryStaffsPresenter extends BaseWidget {
	private Anchor smsLink;
	private String category;

	public CategoryStaffsPresenter(final String category) {
		smsLink = new Anchor("Send message", "javascript:");
		controlsPanel.add(smsLink);
		smsLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				NewSMSPopup smsWidget = NewSMSPopup.INSTANCE;
				smsWidget.setRequestUri("/sms?g=" + category
						+ "All staffs&type=STAFF&tq=select * where "
						+ Fields.Generic.CATEGORY + "='" + category + "'");
				smsWidget.setCapabilities(NewSMSPopup.NONE);
				smsWidget.center();
			}
		});
		this.category = category;
	}

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		MetaDataRepository.get(DocumentType.STAFF,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData result) {
						QueryBuilder builder = new QueryBuilder(
								Utils.sheetVizUrl);
						builder.setMetaData(result).addSelections(
								Staff.STAFF_ID, Generic.NAME);
						if (category != null) {
							builder.addFilter(Generic.CATEGORY, Operator.EQ,
									category);
						}
						request = new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(builder.get());
						callback.onSuccess(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse gResponse) {
		QueryResponse response = gResponse.getQueryResponse();
		DataTable table = (DataTable) response.getDataTable();
		int rows = table.getNumberOfRows();
		for (int i = 0; i < rows; i++) {
			FlowPanel sinContainerPanel = new FlowPanel();
			sinContainerPanel.setStyleName("clearfix span-6 mb3");
			if ((i + 1) % 4 == 0) {
				sinContainerPanel.addStyleName("last");
			}
			if (i % 4 == 0) {
				sinContainerPanel.addStyleName("clear");
			}
			FlowPanel sinInnerPanel = new FlowPanel();
			sinInnerPanel.setStyleName("br1 p5 clearfix");
			String staffId = table.getValue(i, 0, false);
			Image staffImage = new Image("/photo?size=72&id=" + staffId);
			staffImage.setStyleName("fl mr4");
			sinInnerPanel.add(staffImage);
			Label nameLabel = new Label(table.getValue(i, 1));
			nameLabel.setStyleName("strong name");
			sinInnerPanel.add(nameLabel);
			Hyperlink staffLink = new Hyperlink("View profile", "staff/"
					+ staffId);
			staffLink.setStyleName("clearfix h11");
			sinInnerPanel.add(staffLink);
			sinContainerPanel.add(sinInnerPanel);
			contentsPanel.add(sinContainerPanel);
		}
	}

}
