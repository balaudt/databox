package com.sagarius.goddess.client.view.settings;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.util.EntryCollection;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.settings.FormPanel.ElementValue;
import com.sagarius.goddess.client.view.settings.FormPanel.FormElement;
import com.sagarius.goddess.client.view.util.LoadingPanel;
import com.sagarius.goddess.client.view.util.MessagePopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class AcademicPage extends BaseWidget {
	private ListBox yearSelector;
	private FlowPanel mainPanel = new FlowPanel();
	private String defaultYear;

	public AcademicPage() {
		yearSelector = new ListBox(false);
		Button newButton = new Button("New Academic Year");
		newButton.setStyleName("gbutton");
		Button saveButton = new Button("Save");
		saveButton.setStyleName("gbutton");
		FormPanel topPanel = new FormPanel()
				.addElement(
						new FormElement()
								.setName("Academic Year:")
								.setValue(
										new ElementValue()
												.setWidget(yearSelector)
												.setMeta(
														"Select current academic year.")))
				.addElement(
						new FormElement().setName("&nbsp;").setValue(
								new ElementValue().setWidget(saveButton)
										.setWidget(newButton)))
				.addWidget(new HTML("<hr /><hr class='space' />"));
		mainPanel.add(topPanel);
		HTML helpHtml = new HTML(
				"<p>You can select the current academic year in this page.</p><p>1. Multiple academic year will be shown only if the data is available for the same.</p><p>2. Changing the academic year will change the state of the entire school to the selected year. Including the class, students and teachers.</p><p>3. Teachers and students in one academic year may not be available in the other years.</p><p>4. Changing this setting will not delete any data. Hence it is safe to change it.</p><p>5. Once changed, the setting can be brought back to earlier stage again.</p><p>For further information on yearly settings, contact technical support.</p><p>Not all files are accessable to everyone. If you have trouble viewing any of the files listed here, <span style='color: red;'>please contact the administrator for assistance</span>.</p><p><strong>Other files:</strong> For access to Examination and Attendance files for a desired academic year, first set the desired academic year and visit the \"Examination\" or \"Attendance\" page.</p>");
		helpHtml.setStyleName("box-preset-2");
		FlowPanel helpContainer = new FlowPanel();
		helpContainer.setStyleName("local-help fr pt40 pb5");
		helpContainer.add(helpHtml);
		mainPanel.add(helpContainer);

		Set<DocumentType> mainTypes = new HashSet<DocumentType>();
		mainTypes.add(DocumentType.STAFF);
		mainTypes.add(DocumentType.CLASS);
		mainTypes.add(DocumentType.SINGLE_STUDENT);
		mainTypes.add(DocumentType.CSSREL);
		Map<DocumentType, String> docNames = new Maps.ImmutableHashMap<DocumentType, String>()
				.put(DocumentType.STAFF, "Staff List:")
				.put(DocumentType.CLASS, "Class List:")
				.put(DocumentType.SINGLE_STUDENT, "Student List:")
				.put(DocumentType.CSSREL, "Relations:").build();
		Map<DocumentType, String> docHelps = new Maps.ImmutableHashMap<DocumentType, String>()
				.put(DocumentType.STAFF,
						"Contains the list all staff in the school.")
				.put(DocumentType.CLASS,
						"Contains the list of all the classes and the respective faculty members.")
				.put(DocumentType.SINGLE_STUDENT,
						"Contains the list all students in the school.")
				.put(DocumentType.CSSREL,
						"Specifies the relationship b/w the class, staff and subject.")
				.build();
		final Map<DocumentType, Anchor[]> docLinks = new HashMap<DocumentType, Anchor[]>();
		final Map<DocumentType, HTML> docLabels = new HashMap<DocumentType, HTML>();
		for (DocumentType type : mainTypes) {
			docLinks.put(type, new Anchor[] {
					new Anchor("View file", "javascript:", "_blank"),
					new Anchor("Edit file", "javascript:", "_blank") });
			HTML html = new HTML();
			html.setStyleName("filename");
			docLabels.put(type, html);
		}
		FormPanel bottomPanel = new FormPanel()
				.addElement(new FormElement()
						.setName("&nbsp;")
						.setValue(
								new ElementValue()
										.setWidget(new HTML(
												"<strong>Files listed below represent the state of the institution for the academic year selected above.</strong>"))));
		for (DocumentType type : mainTypes) {
			ElementValue elementValue = new ElementValue().setWidget(docLabels
					.get(type));
			bottomPanel.addElement(new FormElement()
					.setName(docNames.get(type)).setValue(elementValue));
			Anchor[] links = docLinks.get(type);
			for (Anchor link : links) {
				elementValue.setWidget(link);
			}
			elementValue.setMeta(docHelps.get(type));
		}
		mainPanel.add(bottomPanel);

		yearSelector.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String yearKey = yearSelector.getValue(yearSelector
						.getSelectedIndex());
				Singletons.getClient().handle(
						new Request(Method.GET, "/documents?year=" + yearKey),
						new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								try {
									String entityText = response.getEntity()
											.getText();
									EntryCollection collection = EntryCollection
											.create(entityText);
									int size = collection.size();
									LinkedList<Document> result = new LinkedList<Document>();
									for (int i = 0; i < size; i++) {
										result.add(new Document(collection
												.get(i)));
									}
									Collection<Anchor[]> allLinks = docLinks
											.values();
									for (Anchor[] links : allLinks) {
										for (Anchor link : links) {
											link.setVisible(false);
										}
									}
									for (Document document : result) {
										DocumentType type = document.getType();
										HTML label = docLabels.get(type);
										if (label == null) {
											continue;
										}
										label.setHTML(document.getDocTitle());
										Anchor[] links = docLinks.get(type);
										links[0].setVisible(true);
										links[1].setVisible(true);
										links[0].setHref(Utils.sheetViewUrl
												.concat("?key=")
												.concat(document.getCccKey())
												.concat("&output=html"));
										links[1].setHref(Utils.sheetViewUrl
												.concat("?key=").concat(
														document.getCccKey()));
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
			}
		});

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String yearKey = yearSelector.getValue(yearSelector
						.getSelectedIndex());
				if (yearKey.equals(defaultYear)) {
					return;
				}
				Singletons.getClient().handle(
						new Request(Method.PUT, "/year?key=" + yearKey),
						new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								Window.Location.reload();
							}
						});
			}
		});

		newButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final PopupPanel newPanel = new PopupPanel(false, true);
				Grid newGrid = new Grid(2, 2);
				newGrid.setText(0, 0, "Year name");
				final TextBox yearNameBox = new TextBox();
				newGrid.setWidget(0, 1, yearNameBox);
				newGrid.setWidget(1, 0, new Button("Save", new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						LoadingPanel.INSTANCE.setMessage("Creating year...")
								.center();
						Singletons.getClient().handle(
								new Request(Method.POST, "/year?name="
										+ yearNameBox.getText()),
								new Uniform() {

									@Override
									public void handle(Request request,
											Response response) {
										LoadingPanel.INSTANCE.hide();
										newPanel.hide();
										if (response.getStatus().isError()) {
											MessagePopup.INSTANCE
													.setMessage(
															"There has been a problem. Try after some time.")
													.center();
											return;
										}
										refresh();
									}
								});
					}
				}));
				newGrid.setWidget(1, 1, new Button("Cancel",
						new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								newPanel.hide();
							}
						}));
				newPanel.setWidget(newGrid);
				newPanel.center();
			}
		});

		refresh();
	}

	@Override
	protected void initialize(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		request = new GenericRequest().setType(RequestType.HTTP)
				.setHttpRequest(new Request(Method.GET, "/year"));
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse genericResponse) {
		try {
			String json = genericResponse.getResponse().getEntity().getText();
			JSONArray result = (JSONArray) JSONParser.parse(json);
			int size = result.size();
			yearSelector.clear();
			for (int i = 0; i < size; i++) {
				JSONArray singleYear = (JSONArray) result.get(i);
				String yearDisplayString = ((JSONString) singleYear.get(1))
						.stringValue();
				String yearKeyString = ((JSONString) singleYear.get(0))
						.stringValue();
				boolean isDefault = ((JSONBoolean) singleYear.get(2))
						.booleanValue();
				if (isDefault) {
					yearSelector.setSelectedIndex(i);
					defaultYear = yearKeyString;
				}
				yearSelector.addItem(yearDisplayString, yearKeyString);
			}
			contentsPanel.add(mainPanel);

			DomEvent.fireNativeEvent(com.google.gwt.dom.client.Document.get()
					.createChangeEvent(), yearSelector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
