package com.sagarius.goddess.client.utils;

import java.io.IOException;
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
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;

public class MetaDataRepository {
	private static final Map<DocumentType, DocumentMetaData> MAIN_DOCS_REPOSITORY = new HashMap<DocumentType, DocumentMetaData>();
	private static final Map<String, DocumentMetaData> DOCS_REPOSITORY = new HashMap<String, DocumentMetaData>();
	private static final Map<String, String> ATTENDANCE_KEYS = new HashMap<String, String>();

	public static final String getAttendanceKey(String clazz) {
		return ATTENDANCE_KEYS.get(clazz);
	}

	public static final void initAttendance(final AsyncCallback<Void> callback) {
		getDocuments(DocumentType.ATTENDANCE,
				new AsyncCallback<List<Document>>() {

					@Override
					public void onSuccess(List<Document> result) {
						for (Document document : result) {
							ATTENDANCE_KEYS.put(document.getParam1(),
									document.getCccKey());
						}
						callback.onSuccess(null);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	public static final void getDocuments(DocumentType type,
			final AsyncCallback<List<Document>> callback) {
		Singletons.getClient().handle(
				new Request(Method.GET, "/documents?type=" + type.toString()),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						if (response.getStatus().isError()) {
							callback.onFailure(null);
							return;
						}
						try {
							String entityText = response.getEntity().getText();
							EntryCollection collection = EntryCollection
									.create(entityText);
							int size = collection.size();
							LinkedList<Document> results = new LinkedList<Document>();
							for (int i = 0; i < size; i++) {
								results.add(new Document(collection.get(i)));
							}
							callback.onSuccess(results);
						} catch (Exception e) {
							callback.onFailure(e);
						}
					}
				});
	}

	public static final void put(DocumentType type, DocumentMetaData data) {
		MAIN_DOCS_REPOSITORY.put(type, data);
	}

	public static final void getSheetMeta(final String key,
			final AsyncCallback<DocumentMetaData> callback,
			final String... fields) {
		Query query = Query.create(new QueryBuilder(Utils.sheetVizUrl)
				.setKey(key).setRowLimit(0).get());
		query.send(new Callback() {

			@Override
			public void onResponse(QueryResponse response) {
				if (response.isError()) {
					callback.onFailure(new Throwable(response
							.getDetailedMessage()));
					return;
				}
				DocumentMetaData metaData = new DocumentMetaData();
				metaData.setCccKey(key);
				DataTable dataTable = response.getDataTable();
				metaData.setFieldMeta(dataTable, fields);
				callback.onSuccess(metaData);
			}
		});
	}

	public static final void get(DocumentType type,
			final AsyncCallback<DocumentMetaData> callback) {
		DocumentMetaData metaData = MAIN_DOCS_REPOSITORY.get(type);
		if (metaData != null) {
			callback.onSuccess(metaData);
			return;
		}
		Singletons.getClient().handle(new Request(Method.GET, "/documents"),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						if (response.getStatus().isError()) {
							callback.onFailure(null);
							return;
						}
						String entityText;
						try {
							entityText = response.getEntity().getText();
							EntryCollection collection = EntryCollection
									.create(entityText);
							final Document document = new Document(collection
									.get(0));
							AsyncCallback<DocumentMetaData> cacher = new AsyncCallback<DocumentMetaData>() {

								@Override
								public void onSuccess(DocumentMetaData result) {
									MAIN_DOCS_REPOSITORY.put(
											document.getType(), result);
									callback.onSuccess(result);
								}

								@Override
								public void onFailure(Throwable caught) {
									callback.onFailure(caught);
								}
							};
							switch (document.getType()) {
							case CLASS:
								getSheetMeta(document.getCccKey(), cacher,
										Fields.CLASS_META);
								break;
							case STAFF:
								getSheetMeta(document.getCccKey(), cacher,
										Fields.STAFF_META);
								break;
							case SINGLE_STUDENT:
								getSheetMeta(document.getCccKey(), cacher,
										Fields.STUDENT_META);
								break;
							case CSSREL:
								getSheetMeta(document.getCccKey(), cacher,
										Fields.RELATIONS_META);
							}
						} catch (IOException e) {
							e.printStackTrace();
							callback.onFailure(e);
							return;
						}
					}
				});
	}

	public static final void put(String key, DocumentMetaData data) {
		DOCS_REPOSITORY.put(key, data);
	}

	public static final void get(final String key,
			final AsyncCallback<DocumentMetaData> callback) {
		DocumentMetaData metaData = DOCS_REPOSITORY.get(key);
		if (metaData != null) {
			callback.onSuccess(metaData);
			return;
		}
		AsyncCallback<DocumentMetaData> cacher = new AsyncCallback<DocumentMetaData>() {

			@Override
			public void onSuccess(DocumentMetaData result) {
				DOCS_REPOSITORY.put(key, result);
				callback.onSuccess(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		};
		getSheetMeta(key, cacher);
	}
}
