package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.DocumentMetaData.FieldMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class StaffsPresenter extends BaseWidget implements CrumbHandler {
	protected TabPanel categorySwitcher = new TabPanel();
	private QueryBuilder builder;

	public StaffsPresenter() {
		categorySwitcher.setStyleName("tabp-preset-1");
		refresh();
	}

	@Override
	public void handleCrumb() {
		((Label) Breadcrumb.getCrumbAt(1, false)).setText("Staffs");
		Breadcrumb.ensureMaxLength(2);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Staffs");
		}
		com.sagarius.goddess.client.Utils.setActiveMainMenu("staff");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		MetaDataRepository.get(DocumentType.STAFF,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData result) {
						builder = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(result);
						FieldMetaData staffId = builder.getMetaData()
								.getFieldMeta(Staff.STAFF_ID);
						String requestUri = builder
								.addSelection(
										"count(" + staffId.getVizQueryId()
												+ ")", true)
								.addSelection(Generic.CATEGORY)
								.setGroup(Generic.CATEGORY).get();
						request = new GenericRequest().setType(
								RequestType.VISUALIZATION)
								.setVisualizationQuery(requestUri);
						categorySwitcher
								.addSelectionHandler(new SelectionHandler<Integer>() {

									@Override
									public void onSelection(
											SelectionEvent<Integer> event) {
										CategoryStaffsPresenter staffsPresenter = (CategoryStaffsPresenter) categorySwitcher
												.getWidget(event
														.getSelectedItem());
										if (!staffsPresenter.isLoaded()) {
											staffsPresenter.refresh();
										}
									}
								});
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
		categorySwitcher.clear();
		contentsPanel.add(categorySwitcher);
		DataTable table = (DataTable) response.getDataTable();
		int rows = table.getNumberOfRows();
		int i;
		for (i = 0; i < rows; i++) {
			String category = table.getValue(i, 1);
			categorySwitcher.insert(new CategoryStaffsPresenter(category),
					category.equals("") ? "Others" : category, i);
		}
		categorySwitcher.insert(new CategoryStaffsPresenter(null), "All", i);
		categorySwitcher.selectTab(0);
		((CategoryStaffsPresenter) categorySwitcher.getWidget(0)).refresh();
	}
}
