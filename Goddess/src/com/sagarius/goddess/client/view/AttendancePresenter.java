package com.sagarius.goddess.client.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.data.Form;
import org.restlet.client.data.Method;
import org.restlet.client.data.Reference;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestSynchronizer;
import com.sagarius.goddess.client.ajax.RequestSynchronizer.RequestGenerator;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class AttendancePresenter extends BaseWidget implements CrumbHandler {
	private Map<String, String> attendance = new HashMap<String, String>();
	// false if view, true if edit
	private boolean state;
	private List<Anchor> links = new ArrayList<Anchor>();

	public AttendancePresenter() {
		categorySwitcher.setStyleName("tabp-preset-1");
		refresh();
	}

	protected TabPanel categorySwitcher = new TabPanel();

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		RequestSynchronizer synchronizer = new RequestSynchronizer(
				new GenericRequest().setType(RequestType.HTTP).setHttpRequest(
						new Request(Method.GET, "/documents?type="
								+ DocumentType.ATTENDANCE))).add(
				new RequestGenerator() {

					@Override
					public GenericRequest getRequest(GenericResponse response) {
						try {
							String responseString = response.getResponse()
									.getEntity().getText();
							List<Document> attendanceDocs = Utils
									.getDocuments(responseString);
							for (Document document : attendanceDocs) {
								attendance.put(document.getParam1(),
										document.getCccKey());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						return new GenericRequest().setType(
								RequestType.DOCUMENT_META).setTypeQuery(
								DocumentType.CLASS);
					}
				}).setCallback(new AsyncCallback<GenericResponse>() {

			@Override
			public void onSuccess(GenericResponse result) {
				DocumentMetaData clazzMeta = result.getMetaData();
				QueryBuilder builder = new QueryBuilder(Utils.sheetVizUrl)
						.setMetaData(clazzMeta)
						.addSelection(Generic.CATEGORY)
						.addSelection(
								"count("
										+ clazzMeta.getFieldMeta(
												Generic.STANDARD)
												.getVizQueryId() + ")", true)
						.setGroup(Generic.CATEGORY);
				String requestUri = builder.get();
				request = new GenericRequest().setType(
						RequestType.VISUALIZATION).setVisualizationQuery(
						requestUri);
				callback.onSuccess(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
		synchronizer.start();
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse genericResponse) {
		QueryResponse response = genericResponse.getQueryResponse();
		categorySwitcher.clear();
		contentsPanel.add(categorySwitcher);
		DataTable table = (DataTable) response.getDataTable();
		int rows = table.getNumberOfRows();
		int i;
		for (i = 0; i < rows; i++) {
			String category = table.getValue(i, 0);
			categorySwitcher.insert(new SingleCategoryPresenter(category),
					category, i);
		}
		categorySwitcher.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int index = categorySwitcher.getDeckPanel().getVisibleWidget();
				SingleCategoryPresenter visibleWidget = (SingleCategoryPresenter) categorySwitcher
						.getWidget(index);
				if (!visibleWidget.isLoaded()) {
					visibleWidget.refresh();
				}
			}
		});
		categorySwitcher.insert(new SingleCategoryPresenter(null), "All", i);
		categorySwitcher.selectTab(0);
	}

	class SingleCategoryPresenter extends BaseWidget {
		private String category;
		private QueryBuilder builder;

		public SingleCategoryPresenter(String category) {
			this.category = category;
			header.addStyleName("actions mb2 fr");
			final Button viewButton = new Button("View Register");
			viewButton.setStyleName("gbutton pillbox pillbox-l join-r");
			final Button editButton = new Button("Change Register");
			editButton.setStyleName("gbutton pillbox pillbox-r");
			viewButton.setEnabled(false);

			editButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					DomEvent.fireNativeEvent(com.google.gwt.dom.client.Document
							.get().createBlurEvent(), viewButton);
					viewButton.setEnabled(true);
					editButton.setEnabled(false);
					for (Anchor link : links) {
						Reference reference = new Reference(URL.decode(link
								.getHref()));
						if (reference.getQueryAsForm().getFirstValue("output") != null) {
							Form form = reference.getQueryAsForm();
							form.removeAll("output");
							reference.setQuery("");
							Set<String> names = form.getNames();
							for (String name : names) {
								String[] values = form.getValuesArray(name);
								for (String value : values) {
									reference.addQueryParameter(name, value);
								}
							}
							link.setHref(reference.toString());
						}
					}
				}
			});
			header.add(viewButton);
			viewButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					DomEvent.fireNativeEvent(com.google.gwt.dom.client.Document
							.get().createBlurEvent(), editButton);
					editButton.setEnabled(true);
					viewButton.setEnabled(false);
					for (Anchor link : links) {
						Reference reference = new Reference(URL.decode(link
								.getHref()));
						if (reference.getQueryAsForm().getFirstValue("output") == null) {
							link.setHref(link.getHref().concat("&output=html"));
						}
					}
				}
			});
			header.add(editButton);
		}

		@Override
		protected void initialize(final AsyncCallback<Void> callback) {
			MetaDataRepository.get(DocumentType.CLASS,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result).addSelections(
											Generic.STANDARD, Generic.SECTION);
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
			builder.removeFilters();
			if (category != null) {
				builder.addFilter(Generic.CATEGORY, Operator.EQ, category);
			}
			String requestUri = builder.get();
			request = new GenericRequest().setType(RequestType.VISUALIZATION)
					.setVisualizationQuery(requestUri);
			callback.onSuccess(null);
		}

		@Override
		public void responseReceived(GenericResponse genericResponse) {
			DataTable table = (DataTable) genericResponse.getQueryResponse()
					.getDataTable();
			int rows = table.getNumberOfRows();
			Map<String, Integer> standardRow = new HashMap<String, Integer>();
			FlexTable resultTable = new FlexTable();
			resultTable.setStyleName("table-preset-4");
			links.clear();
			for (int i = 0; i < rows; i++) {
				String standard = table.getValue(i, 0);
				String section = table.getValue(i, 1);
				String attendKey = attendance.get(standard + "-" + section);
				if (attendKey == null) {
					continue;
				}
				Integer index = standardRow.get(standard);
				if (index == null) {
					index = resultTable.getRowCount();
					standardRow.put(standard, index);
					resultTable.setWidget(index, 0, new HTML(standard));
				}
				int colCount = resultTable.getCellCount(index);
				String linkUrl = Utils.sheetViewUrl.concat("?key=").concat(
						attendKey);
				if (!state) {
					linkUrl += "&output=html";
				}
				Anchor examLink = new Anchor(section, linkUrl, "_blank");
				links.add(examLink);
				resultTable.setWidget(index, colCount, examLink);
				contentsPanel.add(resultTable);
			}
		}
	}

	@Override
	public void handleCrumb() {
		Label crumbLabel = (Label) Breadcrumb.getCrumbAt(1, false);
		crumbLabel.setText("Attendance");
		Utils.setActiveMainMenu("attendance");
		HiddenRowButtons.clear();
		Breadcrumb.ensureMaxLength(2);
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}
}
