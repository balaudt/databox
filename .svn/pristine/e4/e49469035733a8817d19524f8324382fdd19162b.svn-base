package com.sagarius.goddess.client.present;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.gdata.client.GData;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gwt.gdata.client.spreadsheet.WorksheetEntry;
import com.google.gwt.gdata.client.spreadsheet.WorksheetFeed;
import com.google.gwt.gdata.client.spreadsheet.WorksheetFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.visualization.DataTable;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.MetaDataRepository;

public class NewSpreadsheetUtils {
	private static Map<String, String> staffMap = new HashMap<String, String>();

	public static void getNewStaffMap(boolean refresh,
			final AsyncCallback<Map<String, String>> callback) {
		if (!staffMap.isEmpty() && !refresh) {
			callback.onSuccess(staffMap);
			return;
		}
		MetaDataRepository.get(DocumentType.STAFF,
				new AsyncCallback<DocumentMetaData>() {

					@Override
					public void onSuccess(DocumentMetaData result) {
						String queryString = new QueryBuilder(Utils.sheetVizUrl)
								.setMetaData(result)
								.addSelections(Staff.STAFF_ID, Generic.NAME)
								.get();
						Query query = Query.create(queryString);
						query.send(new Callback() {

							@Override
							public void onResponse(QueryResponse response) {
								if (response.isError()) {
									callback.onFailure(new Throwable(response
											.getDetailedMessage()));
									return;
								}
								staffMap.clear();
								DataTable table = (DataTable) response
										.getDataTable();
								int numberOfRows = table.getNumberOfRows();
								for (int i = 0; i < numberOfRows; i++) {
									staffMap.put(table.getValue(i, 0),
											table.getValue(i, 1));
								}
								callback.onSuccess(staffMap);
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});

	}

	public static void getWorkSheetByTitle(final String spreadsheetKey,
			final String title, final AsyncCallback<String> callback) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				FeedURLFactory factory = FeedURLFactory.getDefault();
				String worksheetFeedUrl = factory.getWorksheetFeedUrl(
						spreadsheetKey, "private", "full");
				WorksheetQuery query = WorksheetQuery
						.newInstance(worksheetFeedUrl);
				query.setTitleExact(true);
				query.setTitleQuery(title);
				SpreadsheetService service = SpreadsheetService
						.newInstance("Goddess");
				service.getWorksheetFeed(query, new WorksheetFeedCallback() {

					@Override
					public void onSuccess(WorksheetFeed result) {
						WorksheetEntry[] entries = result.getEntries();
						if (entries == null || entries.length == 0) {
							callback.onSuccess(null);
							return;
						}
						String[] splitId = entries[0].getId().getValue()
								.split("/");
						callback.onSuccess(splitId[splitId.length - 1]);
					}

					@Override
					public void onFailure(CallErrorException caught) {
						callback.onFailure(caught);
					}
				});
			}
		};
		GData.loadGDataApi(null, runnable);
	}
}
