package com.sagarius.goddess.client.view.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.model.util.BaseEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.neuf.ShortMessage;
import com.sagarius.goddess.client.utils.Pager;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class SentMessages extends Composite {
	private List<String> parentKeys = new ArrayList<String>();
	private static SentMessagesUiBinder uiBinder = GWT
			.create(SentMessagesUiBinder.class);
	private SubMessages subMessages = new SubMessages();

	class SubMessages extends PopupPanel {
		private FlexTable subTable;
		private String parentKey;
		private BaseWidget mainPanel = new BaseWidget() {

			@Override
			public void responseReceived(GenericResponse genericResponse) {
				Response response = genericResponse.getResponse();
				try {
					JSONArray value = (JSONArray) JSONParser.parse(response
							.getEntity().getText());
					subTable.clear();
					int size = value.size();
					for (int i = 0; i < size; i++) {
						JSONArray messageJson = (JSONArray) value.get(i);
						ShortMessage message = new ShortMessage(
								(BaseEntry) (messageJson.getJavaScriptObject()));
						Date timestamp = message.getMeta().getTimestamp();
						subTable.setWidget(i + 1, 0, new HTML(
								Utils.DATE_FORMATTER.format(timestamp)));
						subTable.setWidget(i + 1, 1, new HTML(
								Utils.TIME_FORMATTER.format(timestamp)));
						subTable.setWidget(i + 1, 2,
								new HTML(message.getPhone()));
						subTable.setWidget(i + 1, 3, new HTML(message.getId()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				contentsPanel.add(mainGrid);
			}

			@Override
			protected void initialize(AsyncCallback<Void> callback) {
				callback.onSuccess(null);
			}

			@Override
			protected void buildRequest(AsyncCallback<Void> callback) {
				if (request == null) {
					request = new GenericRequest().setType(RequestType.HTTP);
				}
				request.setHttpRequest(new Request(Method.GET, "/sms?parent="
						+ parentKey));
				callback.onSuccess(null);
			}
		};
		private Grid mainGrid;

		public SubMessages() {
			mainGrid = new Grid(2, 1);
			subTable = new FlexTable();
			subTable.setStyleName("table-preset-2");
			subTable.setHTML(0, 0, "<b>Date</b>");
			subTable.setHTML(0, 1, "<b>Time</b>");
			subTable.setHTML(0, 2, "<b>Mobile</b>");
			subTable.setHTML(0, 3, "<b>Status</b>");
			mainGrid.setWidget(0, 0, subTable);
			mainGrid.setWidget(1, 0, new Button("Close", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
			}));
			setWidget(mainPanel);
		}

		public void refresh(String parentKey) {
			this.parentKey = parentKey;
			mainPanel.refresh();
			center();
		}

	}

	public void isLoading(boolean isLoading) {
		if (isLoading) {
			decker.showWidget(0);
		} else {
			decker.showWidget(1);
		}
	}

	private Pager pager = new Pager("/sms") {

		@Override
		public void handle(JSONValue value) {
			isLoading(true);
			JSONArray messageObjects = (JSONArray) value;
			if (messageObjects == null) {
				isLoading(false);
				return;
			}
			mainTable.clear();
			parentKeys.clear();
			int size = messageObjects.size();
			for (int i = 0; i < size; i++) {
				JSONArray messageJson = (JSONArray) messageObjects.get(i);
				ShortMessage message = new ShortMessage(
						(BaseEntry) (messageJson.getJavaScriptObject()));
				Date timestamp = message.getMeta().getTimestamp();
				mainTable.setWidget(i + 1, 0,
						new HTML(Utils.DATE_FORMATTER.format(timestamp)));
				mainTable.setWidget(i + 1, 1,
						new HTML(Utils.TIME_FORMATTER.format(timestamp)));
				mainTable.setWidget(i + 1, 2, new HTML(message.getGroup()));
				mainTable.setWidget(i + 1, 3, new HTML(message.getMessage()));
				parentKeys.add(message.getRefId());
			}
			isLoading(false);
		}

		@Override
		protected void emptySet() {
			mainTable.clear();
			mainTable.setText(1, 0, "No messages sent");
		}

		@Override
		protected String getResourceName() {
			return "sms";
		}

		@Override
		protected void singlePage() {
			nextButton.setVisible(false);
			previousButton.setVisible(false);
		}
	};

	@UiField
	FlexTable mainTable;
	@UiField
	Image previousButton;
	@UiField
	Image nextButton;
	@UiField
	DeckPanel decker;
	@UiField
	Image loadImage;
	@UiField
	Anchor newLink;

	interface SentMessagesUiBinder extends UiBinder<Widget, SentMessages> {
	}

	@SuppressWarnings("unused")
	@UiHandler("previousButton")
	void previous(ClickEvent event) {
		if (!pager.hasPrevious()) {
			return;
		}
		pager.previous();
	}

	@SuppressWarnings("unused")
	@UiHandler("nextButton")
	void next(ClickEvent event) {
		if (!pager.hasNext()) {
			return;
		}
		pager.next();
	}

	public SentMessages() {
		initWidget(uiBinder.createAndBindUi(this));
		loadImage.setSize("auto", "auto");
		mainTable.setHTML(0, 0, "<b>Date</b>");
		mainTable.setHTML(0, 1, "<b>Time</b>");
		mainTable.setHTML(0, 2, "<b>Recipient</b>");
		mainTable.setHTML(0, 3, "<b>Message</b>");
		newLink.setText("[+] New SMS");
		newLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				NewSMSPopup smsPopup = NewSMSPopup.INSTANCE;
				smsPopup.setCapabilities(NewSMSPopup.ANONYMOUS);
				smsPopup.center();
			}
		});
		mainTable.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Cell cell = mainTable.getCellForEvent(event);
				if (cell == null) {
					return;
				}
				int index = cell.getRowIndex();
				if (index <= 0) {
					return;
				}
				String parentKey = parentKeys.get(index - 1);
				if (parentKey == null) {
					return;
				}
				subMessages.refresh(parentKey);
			}
		});
	}

	public void init() {
		pager.refresh();
		if (!pager.hasNext()) {
			previousButton.setVisible(false);
			nextButton.setVisible(false);
		}
	}
}
