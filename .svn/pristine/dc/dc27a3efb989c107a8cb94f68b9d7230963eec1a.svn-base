package com.sagarius.goddess.client.view.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.representation.StringRepresentation;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.utils.Singletons;

public class NewSMSPopup extends PopupPanel {
	public static int NONE = 0x0000;
	public static int TEMPLATE = 0x0001;
	public static int TYPE = 0x0010;
	public static int ANONYMOUS = 0x0100;

	public static final NewSMSPopup INSTANCE = new NewSMSPopup();
	private String requestUri;
	private TextArea messageArea;
	private ListBox box;
	private boolean typeEnabled = false;
	private Map<String, String> templates = new HashMap<String, String>();
	private ListBox templateNames;
	private boolean isAnonymous = false;
	private TextBox anonymousNumbers;
	private Label anonymousLabel;
	private boolean templatesLoaded;

	public void setCapabilities(int capabilities) {
		this.typeEnabled = (TYPE & capabilities) != 0;
		this.isAnonymous = (ANONYMOUS & capabilities) != 0;
		anonymousLabel.setVisible(isAnonymous);
		anonymousNumbers.setVisible(isAnonymous);
		box.setVisible(typeEnabled);
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	@Override
	public void center() {
		super.center();
		anonymousNumbers.setText("");
		messageArea.setText("");
	}

	private void refreshTemplateBox() {
		Singletons.getClient().handle(
				new Request(Method.GET, "/mtemplate?map=true"), new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						templateNames.clear();
						templates.clear();
						if (response.getStatus().isError()) {
							templateNames.addItem("Empty");
							templates.put("Empty", "");
							return;
						}
						try {
							JSONArray array = (JSONArray) JSONParser
									.parse(response.getEntity().getText());
							int size = array.size();
							for (int i = 0; i < size; i++) {
								JSONObject single = (JSONObject) array.get(i);
								String name = ((JSONString) single.get("name"))
										.stringValue();
								String message = ((JSONString) single
										.get("message")).stringValue();
								templateNames.addItem(name);
								templates.put(name, message);
							}
							DomEvent.fireNativeEvent(Document.get()
									.createChangeEvent(), templateNames);
							templatesLoaded = true;
						} catch (IOException e) {
							e.printStackTrace();
							templateNames.addItem("Empty");
							templates.put("Empty", "");
						}
					}
				});
	}

	private NewSMSPopup() {
		FlexTable rootTable = new FlexTable();
		int rowNo = 0;
		templateNames = new ListBox(false);
		templateNames.addItem("Empty");
		templates.put("Empty", "");
		templateNames.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				if (!templatesLoaded) {
					refreshTemplateBox();
					return;
				}
			}
		});
		templateNames.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				messageArea.setText(templates.get(templateNames
						.getItemText(templateNames.getSelectedIndex())));
			}
		});
		rootTable.setWidget(rowNo, 1, templateNames);
		Image refreshButton = new Image("/goddess/images/refresh.png");
		refreshButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refreshTemplateBox();
			}
		});
		rootTable.setWidget(rowNo++, 2, refreshButton);

		anonymousLabel = new Label("Phone number [comma separated]");
		rootTable.setWidget(rowNo, 0, anonymousLabel);
		anonymousNumbers = new TextBox();
		rootTable.setWidget(rowNo++, 1, anonymousNumbers);

		messageArea = new TextArea();
		box = new ListBox(false);
		box.addItem("Staffs");
		box.addItem("Parents");
		rootTable.setWidget(rowNo++, 0, box);
		rootTable.setWidget(rowNo, 0, new Label("Message"));
		rootTable.setWidget(rowNo++, 1, messageArea);
		final Button sendButton = new Button("Send");
		sendButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				sendButton.setEnabled(false);
				String messageText = messageArea.getText();
				if (messageText.length() > 150 || messageText.length() < 1) {
					MessagePopup.INSTANCE
							.setMessage(
									"Message length should be between 1 and 150 characters")
							.center();
					sendButton.setEnabled(false);
					return;
				}
				if (isAnonymous) {
					String numbers = anonymousNumbers.getText();
					if (numbers.trim().length() == 0) {
						MessagePopup.INSTANCE.setMessage(
								"Number list cannot be empty").center();
						sendButton.setEnabled(false);
						return;
					}
					numbers.replaceAll(" ", "");
					Singletons.getClient().handle(
							new Request(Method.POST, "/sms?no=" + numbers,
									new StringRepresentation(messageText)),
							new Uniform() {

								@Override
								public void handle(Request request,
										Response response) {
									if (response.getStatus().isError()) {
										MessagePopup.INSTANCE
												.setMessage(
														"There has been a problem sending your message. Try again after some time")
												.center();
									} else {
										MessagePopup.INSTANCE
												.setMessage(
														"Your message has been sent successfully")
												.center();
									}
									hide();
									sendButton.setEnabled(true);
								}
							});
					return;
				}
				final String curReq;
				if (typeEnabled) {
					curReq = requestUri + "&type=" + getType();
				} else {
					curReq = requestUri;
				}
				Singletons.getClient().handle(
						new Request(Method.POST, curReq,
								new StringRepresentation(messageText)),
						new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								if (response.getStatus().isError()) {
									Window.alert("There has been a problem sending your message. Try again after some time");
								} else {
									Window.alert("Your message has been sent successfully");
								}
								hide();
								sendButton.setEnabled(true);
							}
						});
			}
		});
		rootTable.setWidget(rowNo, 0, sendButton);
		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		rootTable.setWidget(rowNo++, 1, cancelButton);
		setWidget(rootTable);
	}

	public MemberType getType() {
		switch (box.getSelectedIndex()) {
		case 0:
			return MemberType.STAFF;
		case 1:
			return MemberType.PARENT;
		default:
			return null;
		}
	}
}
