package com.sagarius.goddess.client.present;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.model.util.EntryCollection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.Singletons;

public class SpreadsheetUtils {
	private static Map<String, String> staffMap = new HashMap<String, String>();
	private static String domain;

	public static void getQueryString(DocumentType type,
			final String[] selectFields, final String[] whereFields,
			final AsyncCallback<String> callback) {
		getDocumentByType(type, new AsyncCallback<Document>() {

			@Override
			public void onSuccess(Document result) {
				getQueryString(result, selectFields, whereFields, callback);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public static void getQueryString(DocumentType type,
			final String[] selectFields, final AsyncCallback<String> callback) {
		getDocumentByType(type, new AsyncCallback<Document>() {

			@Override
			public void onSuccess(Document result) {
				getQueryString(result, selectFields, null, callback);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public static void getQueryString(Document document,
			final String[] selectFields, final String[] whereFields,
			final AsyncCallback<String> callback) {
		final String cccKey = document.getCccKey();
		getQueryString(cccKey, selectFields, whereFields, null, callback);
	}

	public static void getQueryString(final String cccKey,
			final String[] selectFields, final String[] whereFields,
			final String sheetName, final AsyncCallback<String> callback) {
		final StringBuffer buffer = new StringBuffer(Utils.sheetVizUrl
				+ "?key=").append(cccKey);
		getHeaders(cccKey, sheetName, new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				if (sheetName != null) {
					buffer.append("&sheet=" + sheetName);
				}
				if (selectFields != null) {
					buffer.append("&tq=select ");
					for (String field : selectFields) {
						buffer.append(result.get(field) + ",");
					}
					buffer.deleteCharAt(buffer.length() - 1);
				} else {
					buffer.append("&tq=select * ");
				}
				if (whereFields == null) {
					callback.onSuccess(buffer.toString());
					return;
				}
				buffer.append(" where ");
				for (String field : whereFields) {
					buffer.append(result.get(field)).append("=")
							.append("${" + field + "}").append(" and ");
				}
				int length = buffer.length();
				buffer.delete(length - 5, length);
				callback.onSuccess(buffer.toString());
			}
		});
	}

	public static void getStaffMap(AsyncCallback<Map<String, String>> callback) {
		getStaffMap(false, callback);
	}

	public static void getStaffMap(boolean refresh,
			final AsyncCallback<Map<String, String>> callback) {
		if (!staffMap.isEmpty() && !refresh) {
			callback.onSuccess(staffMap);
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				getQueryString(DocumentType.STAFF, new String[] { "staffid",
						"name" }, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(String result) {
						Query query = Query.create(result);
						query.send(new Callback() {

							@Override
							public void onResponse(QueryResponse response) {
								if (response.isError()) {
									callback.onFailure(new Throwable(response
											.getDetailedMessage()));
									return;
								}
								staffMap.clear();
								DataTable table = response.getDataTable();
								int numberOfRows = table.getNumberOfRows();
								for (int i = 0; i < numberOfRows; i++) {
									staffMap.put(table.getValueInt(i, 0) + "",
											table.getValueString(i, 1));
								}
								callback.onSuccess(staffMap);
							}
						});
					}
				});
			}
		};
		VisualizationUtils.loadVisualizationApi(runnable, Table.PACKAGE);
	}

	private static List<Document> documents = new ArrayList<Document>();

	public static void getDocuments(AsyncCallback<List<Document>> callback) {
		getDocuments(false, callback);
	}

	public static void getDocuments(boolean refresh,
			final AsyncCallback<List<Document>> callback) {
		if (!documents.isEmpty() && !refresh) {
			callback.onSuccess(documents);
			return;
		}
		Singletons.getClient().handle(
				new Request(Method.GET, "/documents?domain=true"),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						try {
							String entityText = response.getEntity().getText();
							EntryCollection collection = EntryCollection
									.create(entityText);
							domain = collection.get(0).get(0, String.class);
							int size = collection.size();
							for (int i = 1; i < size; i++) {
								documents.add(new Document(collection.get(i)));
							}
							callback.onSuccess(documents);
						} catch (IOException e) {
							e.printStackTrace();
							callback.onFailure(e);
						}
					}
				});
	}

	public static void setDomain(String domain) {
		SpreadsheetUtils.domain = domain;
	}

	public static String getDomain() {
		return domain;
	}

	public static void getDocumentByType(final DocumentType type,
			boolean refresh, final AsyncCallback<Document> callback) {
		getDocumentsByType(type, refresh, new AsyncCallback<List<Document>>() {

			@Override
			public void onSuccess(List<Document> result) {
				callback.onSuccess(result.get(0));
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public static void getDocumentByType(final DocumentType type,
			final AsyncCallback<Document> callback) {
		getDocumentByType(type, false, callback);
	}

	public static void getDocumentsByType(final DocumentType type,
			final AsyncCallback<List<Document>> callback) {
		getDocumentsByType(type, false, callback);
	}

	public static void getDocumentsByType(final DocumentType type,
			boolean refresh, final AsyncCallback<List<Document>> callback) {
		getDocuments(refresh, new AsyncCallback<List<Document>>() {

			@Override
			public void onSuccess(List<Document> result) {
				List<Document> documents = new LinkedList<Document>();
				for (Document document : result) {
					if (document.getType().equals(type)) {
						documents.add(document);
					}
				}
				callback.onSuccess(documents);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public static void getHeaders(DocumentType type, final String sheetName,
			final AsyncCallback<Map<String, String>> callback) {
		getDocumentByType(type, new AsyncCallback<Document>() {

			@Override
			public void onSuccess(final Document document) {
				getHeaders(document.getCccKey(), sheetName,
						new AsyncCallback<Map<String, String>>() {

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}

							@Override
							public void onSuccess(Map<String, String> result) {
								result.put("docKey", document.getCccKey());
								callback.onSuccess(result);
							}
						});
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}

	public static void getHeaders(final String key, final String sheetName,
			final AsyncCallback<Map<String, String>> callback) {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				String queryString = Utils.sheetVizUrl + "?key=" + key
						+ "&tq=limit 0";
				if (sheetName != null) {
					queryString += "&sheet=" + sheetName;
				}
				Query query = Query.create(queryString);
				query.send(new Callback() {

					@Override
					public void onResponse(QueryResponse response) {
						if (response.isError()) {
							callback.onFailure(new Throwable(response
									.getDetailedMessage()));
							return;
						}
						Map<String, String> result = new HashMap<String, String>();
						DataTable table = response.getDataTable();
						int columns = table.getNumberOfColumns();
						for (int i = 0; i < columns; i++) {
							String id = table.getColumnId(i);
							String label = table.getColumnLabel(i)
									.toLowerCase().replaceAll(" ", "")
									.replaceAll("_", "");
							result.put(label, id);
						}
						callback.onSuccess(result);
					}
				});
			}
		};
		VisualizationUtils.loadVisualizationApi(runnable, Table.PACKAGE);
	}

	public static String getStringValue(DataTable table, int row, int column) {
		try {
			ColumnType type = table.getColumnType(column);
			switch (type) {
			case BOOLEAN:
				return table.getValueBoolean(row, column) + "";
			case DATE:
			case DATETIME:
				return table.getValueDate(row, column).toString();
			case NUMBER:
				return table.getValueDouble(row, column) + "";
			case STRING:
				return table.getValueString(row, column);
			case TIMEOFDAY:
				return table.getValueTimeOfDay(row, column).toString();
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
