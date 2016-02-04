package com.sagarius.goddess.client.view.util;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.DocumentMetaData.FieldMetaData;
import com.sagarius.goddess.client.utils.Fields.Clazz;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.view.CategoryClassPresenter;
import com.sagarius.goddess.client.widgets.BaseWidget;

public abstract class CategoryStandardsContainer<T extends CategoryClassPresenter>
		extends BaseWidget {
	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		FieldMetaData inchargeMeta = builder.getMetaData().getFieldMeta(
				Clazz.INCHARGE_ID);
		String requestUri = builder
				.removeSelections()
				.addSelection("count(" + inchargeMeta.getVizQueryId() + ")",
						true).addSelection(Generic.CATEGORY).get();
		if (request == null) {
			request = new GenericRequest().setType(RequestType.VISUALIZATION);
		}
		request.setVisualizationQuery(requestUri);
		callback.onSuccess(null);
	}

	@Override
	protected void initialize(final AsyncCallback<Void> callback) {
		MetaDataRepository.get(DocumentType.CLASS,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData result) {
						builder = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(result);
						contentsPanel.add(categorySwitcher);
						categorySwitcher
								.addSelectionHandler(new SelectionHandler<Integer>() {

									@SuppressWarnings("unchecked")
									@Override
									public void onSelection(
											SelectionEvent<Integer> event) {
										T standards = (T) categorySwitcher
												.getWidget(event
														.getSelectedItem());
										if (!standards.isLoaded()) {
											standards.refresh();
										}
									}
								});
						builder.setGroup(Generic.CATEGORY).get();
						callback.onSuccess(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	protected TabPanel categorySwitcher = new TabPanel();
	private QueryBuilder builder;

	@SuppressWarnings("unchecked")
	protected T getCurrentWidget() {
		int visibleIndex = categorySwitcher.getDeckPanel().getVisibleWidget();
		return (T) categorySwitcher.getWidget(visibleIndex);
	}

	public CategoryStandardsContainer() {
		categorySwitcher.setStyleName("tabp-preset-1");
		refresh();
	}

	@Override
	public void responseReceived(GenericResponse inResponse) {
		QueryResponse response = inResponse.getQueryResponse();
		categorySwitcher.clear();
		contentsPanel.add(categorySwitcher);
		DataTable table = (DataTable) response.getDataTable();
		int rows = table.getNumberOfRows();
		int i;
		for (i = 0; i < rows; i++) {
			String category = table.getValue(i, 1);
			categorySwitcher
					.insert(getStandardsInstance(category), category, i);
		}
		categorySwitcher.insert(getStandardsInstance(null), "All", i);
		categorySwitcher.selectTab(0);
	}

	protected abstract T getStandardsInstance(String category);
}
