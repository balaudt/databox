package com.sagarius.goddess.client.view.settings;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.representation.StringRepresentation;
import org.sagarius.radix.client.collect.LinkedHashSetMultiMap;
import org.sagarius.radix.client.view.CheckboxTree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.view.util.LoadingPanel;
import com.sagarius.goddess.client.view.util.MessagePopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class AttendancePage extends BaseWidget {
	private QueryBuilder builder;
	private Map<String, String> attendMap = new HashMap<String, String>();

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		MetaDataRepository.get(DocumentType.CLASS,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData result) {
						builder = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(result).addSelections(
										Generic.STANDARD, Generic.SECTION,
										Generic.CATEGORY);
						callback.onSuccess(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	@Override
	protected void buildRequest(final AsyncCallback<Void> callback) {
		Singletons.getClient().handle(
				new Request(Method.GET, "/documents?type="
						+ DocumentType.ATTENDANCE), new Uniform() {

					@Override
					public void handle(Request inRequest, Response response) {
						if (response.getStatus().isError()) {
							try {
								callback.onFailure(new Throwable(response
										.getEntity().getText()));
							} catch (IOException e) {
								callback.onFailure(new Throwable(
										"Problem connecting to network"));
							}
							return;
						}
						try {
							String entityText = response.getEntity().getText();
							List<Document> documents = Utils
									.getDocuments(entityText);
							for (Document document : documents) {
								attendMap.put(document.getParam1(),
										document.getCccKey());
							}
							request = new GenericRequest().setType(
									RequestType.VISUALIZATION)
									.setVisualizationQuery(builder.get());
							callback.onSuccess(null);
						} catch (IOException e) {
							callback.onFailure(e);
						}
					}
				});
	}

	@Override
	public void responseReceived(GenericResponse genericResponse) {
		DataTable table = (DataTable) genericResponse.getQueryResponse()
				.getDataTable();
		int rows = table.getNumberOfRows();

		HashMap<String, LinkedHashSetMultiMap<String, String>> mainMap = new HashMap<String, LinkedHashSetMultiMap<String, String>>();

		for (int i = 0; i < rows; i++) {
			String standard = table.getValue(i, 0);
			String section = table.getValue(i, 1);
			String category = table.getValue(i, 2);
			String attendKey = attendMap.get(standard.concat("-").concat(
					section));
			if (attendKey == null) {
				LinkedHashSetMultiMap<String, String> categoryMap = mainMap
						.get(category);
				if (categoryMap == null) {
					categoryMap = new LinkedHashSetMultiMap<String, String>();
					mainMap.put(category, categoryMap);
				}
				categoryMap.put(standard, section);
			}
		}

		FlowPanel mainPanel = new FlowPanel();
		Set<Entry<String, LinkedHashSetMultiMap<String, String>>> entrySet = mainMap
				.entrySet();
		final Map<CheckBox, String> allLeaves = new HashMap<CheckBox, String>();
		for (Entry<String, LinkedHashSetMultiMap<String, String>> entry : entrySet) {
			String category = entry.getKey();
			final DisclosurePanel categoryPanel = new DisclosurePanel();
			final CheckBox categoryCheckBox = new CheckBox(category);
			categoryPanel.setHeader(categoryCheckBox);
			CheckboxTree tree = new CheckboxTree(categoryCheckBox);
			final FlexTable contentsTable = new FlexTable();
			Set<Entry<String, Collection<String>>> standards = entry.getValue()
					.entrySet();
			for (Entry<String, Collection<String>> standardMap : standards) {
				String standard = standardMap.getKey();
				final int rowCount = contentsTable.getRowCount();
				int colCount = 0;
				CheckBox standardCheckBox = new CheckBox(standard);
				CheckboxTree standardSubTree = new CheckboxTree(
						standardCheckBox);
				tree.addChild(standardSubTree);
				contentsTable.setWidget(rowCount, colCount++, standardCheckBox);
				Collection<String> sections = standardMap.getValue();
				for (String section : sections) {
					CheckBox sectionCheckBox = new CheckBox(section);
					contentsTable.setWidget(rowCount, colCount++,
							sectionCheckBox);
					standardSubTree.addChild(sectionCheckBox);
					allLeaves.put(sectionCheckBox,
							standard.concat("-").concat(section));
				}
			}
			categoryPanel.setContent(contentsTable);
			mainPanel.add(categoryPanel);
			tree.listen();
		}

		final Button createButton = new Button("Create registers");
		createButton.setStyleName("gbutton");
		mainPanel.add(createButton);
		createButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Set<Entry<CheckBox, String>> leavesEntrySet = allLeaves
						.entrySet();
				JSONArray clazzes = new JSONArray();
				int i = 0;
				for (Entry<CheckBox, String> entry : leavesEntrySet) {
					if (entry.getKey().getValue()) {
						clazzes.set(i++, new JSONString(entry.getValue()));
					}
				}
				JSONObject input = new JSONObject();
				input.put("clazz", clazzes);
				LoadingPanel.INSTANCE.setMessage("Initiating process").center();
				Singletons.getClient().handle(
						new Request(Method.POST, "/attendance",
								new StringRepresentation(input.toString())),
						new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								LoadingPanel.INSTANCE.hide();
								MessagePopup.INSTANCE.setMessage(
										"Process initiated").center();
								History.newItem("attendance", true);
							}
						});
			}
		});
		contentsPanel.add(mainPanel);
	}
}
