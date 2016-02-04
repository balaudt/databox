package com.sagarius.goddess.client.view.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.data.Reference;
import org.sagarius.radix.client.model.util.BaseEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.MessageTemplate;
import com.sagarius.goddess.client.utils.Pager;
import com.sagarius.goddess.client.utils.Singletons;

public class TemplateViewer extends Composite {
	private static TemplateViewerUiBinder uiBinder = GWT
			.create(TemplateViewerUiBinder.class);
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
	private List<String> keys = new ArrayList<String>();
	private boolean isLoaded = false;

	@SuppressWarnings("unused")
	@UiHandler("previousButton")
	void previous(ClickEvent event) {
		if (!pager.hasPrevious()) {
			return;
		}
		pager.previous();
	}

	private ItemBoundWidget boundWidget = new ItemBoundWidget();

	class ItemBoundWidget extends PopupPanel implements Uniform {
		private TextArea messageBox;
		private TextBox nameBox;
		private String key;
		private int index;
		private FlexTable editGrid;
		private FlexTable viewGrid;
		private HTML nameLabel;
		private HTML messageLabel;

		public ItemBoundWidget() {
			editGrid = new FlexTable();
			editGrid.setText(0, 0, "Name");
			nameBox = new TextBox();
			editGrid.setWidget(0, 1, nameBox);
			editGrid.setText(1, 0, "Message");
			messageBox = new TextArea();
			editGrid.setWidget(1, 1, messageBox);
			editGrid.setWidget(2, 0, new Button("Save", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					String templateName = nameBox.getText();
					String templateText = messageBox.getText();
					if (templateName.trim().equals("")
							|| templateName.trim().equals("")) {
						MessagePopup.INSTANCE.setMessage(
								"Both Name and Message are mandatory fields")
								.center();
						return;
					}
					if (templateText.length() > 150) {
						MessagePopup.INSTANCE.setMessage(
								"Message cannot be longer than 150 characters")
								.center();
						return;
					}

					Request request = new Request();
					Reference reference = new Reference("/mtemplate");
					reference.addQueryParameter("name", templateName);
					reference.addQueryParameter("message", templateText);
					request.setResourceRef(reference);
					if (key != null) {
						request.setMethod(Method.PUT);
						reference.addQueryParameter("key", key);
					} else {
						request.setMethod(Method.POST);
					}
					Singletons.getClient()
							.handle(request, ItemBoundWidget.this);
				}
			}));
			editGrid.setWidget(2, 1, new Button("Cancel", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
			}));

			viewGrid = new FlexTable();
			viewGrid.setText(0, 0, "Name");
			nameLabel = new HTML();
			viewGrid.setWidget(0, 1, nameLabel);
			viewGrid.setText(1, 0, "Message");
			messageLabel = new HTML();
			viewGrid.setWidget(1, 1, messageLabel);
			viewGrid.setWidget(2, 0, new Button("Edit", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					setWidget(editGrid);
				}
			}));
			viewGrid.setWidget(2, 1, new Button("Delete", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					final Button sourceButton = (Button) event.getSource();
					sourceButton.setEnabled(false);

					Singletons.getClient()
							.handle(new Request(Method.DELETE,
									"/mtemplate?key=" + key), new Uniform() {

								@Override
								public void handle(Request request,
										Response response) {
									hide();
									init();
									sourceButton.setEnabled(true);
								}
							});
				}
			}));
			viewGrid.setWidget(2, 2, new Button("Cancel", new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
			}));
			setModal(true);
		}

		public void setIndex(int index) {
			this.index = index;
			if (index == 0) {
				nameBox.setText("");
				messageBox.setText("");
				key = null;
				setWidget(editGrid);
			} else {
				nameBox.setText(mainTable.getText(index, 2));
				messageBox.setText(mainTable.getText(index, 3));
				nameLabel.setText(mainTable.getText(index, 2));
				messageLabel.setText(mainTable.getText(index, 3));
				key = keys.get(index - 1);
				setWidget(viewGrid);
			}
			center();
		}

		@Override
		public void handle(Request request, Response response) {
			if (response.getStatus().isError()) {
				MessagePopup.INSTANCE.setMessage(
						"There has been a problem saving.  Try again later")
						.center();
				return;
			}
			if (key == null) {
				pager.setPageNumber(0);
				pager.go();
				hide();
				return;
			}
			try {
				MessageTemplate template = new MessageTemplate(response
						.getEntity().getText());
				Date timestamp = template.getMeta().getTimestamp();
				mainTable.setWidget(index, 0, new HTML(
						Utils.DATE_FORMATTER.format(timestamp)));
				mainTable.setWidget(index, 1, new HTML(
						Utils.TIME_FORMATTER.format(timestamp)));
				mainTable.setWidget(index, 2, new HTML(template.getName()));
				mainTable.setWidget(index, 3, new HTML(template.getMessage()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			hide();
		}
	}

	public TemplateViewer() {
		initWidget(uiBinder.createAndBindUi(this));
		loadImage.setSize("auto", "auto");

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
				boundWidget.setIndex(index);
			}
		});
		newLink.setText("[+] Add New Template");
		newLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				boundWidget.setIndex(0);
			}
		});
		mainTable.setHTML(0, 0, "<b>Date</b>");
		mainTable.setHTML(0, 1, "<b>Time</b>");
		mainTable.setHTML(0, 2, "<b>Name</b>");
		mainTable.setHTML(0, 3, "<b>Message</b>");
	}

	@SuppressWarnings("unused")
	@UiHandler("nextButton")
	void next(ClickEvent event) {
		if (!pager.hasNext()) {
			return;
		}
		pager.next();
	}

	private Pager pager = new Pager("/mtemplate") {
		@Override
		protected void handle(JSONValue value) {
			isLoading(true);
			JSONArray messageObjects = (JSONArray) value;
			if (messageObjects == null) {
				isLoading(false);
				return;
			}
			mainTable.clear();
			keys.clear();
			int size = messageObjects.size();
			for (int i = 0; i < size; i++) {
				JSONArray messageJson = (JSONArray) messageObjects.get(i);
				MessageTemplate template = new MessageTemplate(
						(BaseEntry) messageJson.getJavaScriptObject());
				Date timestamp = template.getMeta().getTimestamp();
				mainTable.setWidget(i + 1, 0, new HTML(
						Utils.DATE_FORMATTER.format(timestamp)));
				mainTable.setWidget(i + 1, 1, new HTML(
						Utils.TIME_FORMATTER.format(timestamp)));
				mainTable.setWidget(i + 1, 2, new HTML(template.getName()));
				mainTable.setWidget(i + 1, 3, new HTML(template.getMessage()));
				keys.add(template.getRefId());
			}
			isLoading(false);
		}

		@Override
		protected String getResourceName() {
			return "mtemplate";
		}

		@Override
		protected void emptySet() {
			mainTable.clear();
			mainTable.setText(1, 0, "No templates");
		}

		@Override
		protected void singlePage() {
			nextButton.setVisible(false);
			previousButton.setVisible(false);
		}
	};

	public void isLoading(boolean isLoading) {
		if (isLoading) {
			decker.showWidget(0);
		} else {
			decker.showWidget(1);
		}
	}

	@UiTemplate("SentMessages.ui.xml")
	interface TemplateViewerUiBinder extends UiBinder<Widget, TemplateViewer> {
	}

	public void init() {
		pager.refresh();
		if (!pager.hasNext()) {
			previousButton.setVisible(false);
			nextButton.setVisible(false);
		}
		isLoaded = true;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

}
