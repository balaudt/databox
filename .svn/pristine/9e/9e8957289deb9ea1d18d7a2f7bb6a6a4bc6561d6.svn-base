package com.sagarius.goddess.client;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Form;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.model.util.EntryCollection;
import org.sagarius.radix.client.view.GWTCProgress;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.gdata.client.GData;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.Listener;
import com.sagarius.goddess.client.ajax.MultiRequestCallback;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.School;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.Fields;
import com.sagarius.goddess.client.utils.MetaDataRepository;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;

public class IndexEntryPoint implements EntryPoint {
	private int progress = 0;
	private static final int INITIAL_NUMBER_OF_REQUESTS = 7;
	private GWTCProgress progressBar = new GWTCProgress(
			INITIAL_NUMBER_OF_REQUESTS, GWTCProgress.SHOW_TEXT);
	private String email;

	@Override
	public void onModuleLoad() {
		progressBar.setText("Loading...");
		progressBar.setWidth(INITIAL_NUMBER_OF_REQUESTS * 50 + "px");
		final RootPanel rootPanel = RootPanel.get();
		rootPanel.add(progressBar);
		progressBar.setProgress(progress++, INITIAL_NUMBER_OF_REQUESTS);
		progressBar.show();
		Singletons.getClient().handle(new Request(Method.HEAD, "/login"),
				new Uniform() {

					@Override
					public void handle(Request request, final Response response) {
						if (response.getStatus().isError()) {
							String redirectUrl = "/login.html?nextUrl="
									+ URL.encode(Window.Location.getHref());
							if (Window.Location.getHostName().equals(
									"127.0.0.1")) {
								redirectUrl += "&gwt.codesvr=127.0.0.1:9997";
							}
							Window.Location.replace(redirectUrl);
							return;
						}
						String loginDataStr = ((Form) response.getAttributes()
								.get("org.restlet.client.http.headers"))
								.getFirstValue("Login-Data");
						String[] loginData = loginDataStr.split(",");
						email = loginData[0];
						String typeString = loginData[1];
						if (loginData.length > 2) {
							Utils.sheetVizUrl = "https://spreadsheets.google.com/a/"
									+ loginData[2] + "/tq";
							Utils.sheetViewUrl = "https://spreadsheets.google.com/a/"
									+ loginData[2] + "/ccc";
						}
						MemberType type = MemberType.valueOf(typeString);
						if (type == MemberType.ADMINISTRATOR
								|| type == MemberType.STAFF) {
							getDocuments();
							getSchoolData();
						} else if (type == MemberType.PARENT) {
							RootPanel.get().remove(progressBar);
							GWT.runAsync(new RunAsyncCallback() {

								@Override
								public void onSuccess() {
									Runnable runnable = new Runnable() {
										public void run() {
											Runnable runnable = new Runnable() {
												public void run() {
													ParentIndexPage indexPage = new ParentIndexPage();
													indexPage.setPhoneNo(email);
													indexPage.initComponents();
													RootPanel.get(
															"outer-container")
															.add(indexPage);
													History.addValueChangeHandler(indexPage);
												}
											};
											VisualizationUtils
													.loadVisualizationApi(
															runnable,
															Table.PACKAGE);
										}
									};
									GData.loadGDataApi(null, runnable);
								}

								@Override
								public void onFailure(Throwable reason) {

								}
							});
						}
					}
				});
	}

	protected void getSchoolData() {
		Singletons.getClient().handle(new Request(Method.GET, "/school"),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						progressBar.setProgress(progress++,
								INITIAL_NUMBER_OF_REQUESTS);
						try {
							Utils.currentSchool = new School(response
									.getEntity().getText());
						} catch (IOException e) {
							e.printStackTrace();
						}
						initComponentsOnComplete();
					}
				});
	}

	protected void getDocuments() {
		Singletons.getClient().handle(new Request(Method.GET, "/documents"),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						try {
							String entityText = response.getEntity().getText();
							progressBar.setProgress(progress++,
									INITIAL_NUMBER_OF_REQUESTS);
							loadMetaData(entityText);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
	}

	protected void loadMetaData(String entityText) {
		final EntryCollection collection = EntryCollection.create(entityText);
		final int size = collection.size();
		for (int i = 0; i < size; i++) {
			Document document = new Document(collection.get(i));
			DocumentType type = document.getType();
			if (type == DocumentType.DUMMY) {
				// Simple workaround for Issue 315 in
				// spreadsheets-visualizations
				IFrameElement iFrame = DOM.createIFrame().cast();
				iFrame.setAttribute("src",
						Utils.sheetVizUrl.replaceAll("tq", "ccc?key=")
								+ document.getCccKey() + "&output=html");
				iFrame.setAttribute("style", "display: none;");
				LoadWrapper element = new LoadWrapper(iFrame) {

					@Override
					public void widgetLoaded() {
						Runnable runnable = new Runnable() {
							public void run() {
								Runnable runnable = new Runnable() {
									public void run() {
										Timer timer = new Timer() {

											@Override
											public void run() {
												// for (int i = 0; i < size;
												// i++) {
												// Document document = new
												// Document(
												// collection.get(i));
												// switch (document.getType()) {
												// case CLASS:
												// case STAFF:
												// case SINGLE_STUDENT:
												// case CSSREL:
												// loadMetaData(document);
												// break;
												// }
												// }
												load(collection);
											}
										};
										timer.schedule(50);
									}
								};
								VisualizationUtils.loadVisualizationApi(
										runnable, Table.PACKAGE);
							}
						};
						GData.loadGDataApi(null, runnable);
					}
				};
				RootPanel.get().add(element);
			}
		}

	}

	void load(EntryCollection collection) {
		int size = collection.size();
		MultiRequestCallback callback = new MultiRequestCallback() {

			@Override
			public void complete() {
				RootPanel.get().remove(progressBar);
				initComponents();
			}
		};
		for (int i = 0; i < size; i++) {
			final Document document = new Document(collection.get(i));
			callback.add(
					new GenericRequest().setType(RequestType.VISUALIZATION)
							.setVisualizationQuery(
									new QueryBuilder(Utils.sheetVizUrl)
											.setKey(document.getCccKey())
											.setRowLimit(0).get()),
					new AsyncCallback<GenericResponse>() {

						@Override
						public void onSuccess(GenericResponse result) {
							DocumentMetaData metaData = new DocumentMetaData();
							metaData.setCccKey(document.getCccKey());
							metaData.setSpreadsheetKey(document
									.getDocumentKey());
							metaData.setDisplayName(document.getDocName());
							DataTable dataTable = result.getQueryResponse()
									.getDataTable();
							switch (document.getType()) {
							case CLASS:
								metaData.setFieldMeta(dataTable,
										Fields.CLASS_META);
								break;
							case STAFF:
								metaData.setFieldMeta(dataTable,
										Fields.STAFF_META);
								break;
							case SINGLE_STUDENT:
								metaData.setFieldMeta(dataTable,
										Fields.STUDENT_META);
								break;
							case CSSREL:
								metaData.setFieldMeta(dataTable,
										Fields.RELATIONS_META);
								break;
							}
							MetaDataRepository.put(document.getType(), metaData);
						}

						@Override
						public void onFailure(Throwable caught) {
							// Meta data has failed for a single document type
						}
					});
		}
		callback.addListener(new Listener() {

			@Override
			public void stateChanged(short status) {
				progressBar.setProgress(progress + status,
						INITIAL_NUMBER_OF_REQUESTS);
			}
		});
		callback.start();
	}

	protected void loadMetaData(final Document document) {
		Runnable runnable = new Runnable() {
			public void run() {
				Query query = Query.create(new QueryBuilder(Utils.sheetVizUrl)
						.setKey(document.getCccKey()).setRowLimit(0).get());
				query.send(new Callback() {

					@Override
					public void onResponse(QueryResponse response) {
						if (response.isError()) {
							progressBar.setProgress(progress++,
									INITIAL_NUMBER_OF_REQUESTS);
							initComponentsOnComplete();
							return;
						}
						DocumentMetaData metaData = new DocumentMetaData();
						metaData.setCccKey(document.getCccKey());
						metaData.setSpreadsheetKey(document.getDocumentKey());
						metaData.setDisplayName(document.getDocName());
						DataTable dataTable = response.getDataTable();
						switch (document.getType()) {
						case CLASS:
							metaData.setFieldMeta(dataTable, Fields.CLASS_META);
							break;
						case STAFF:
							metaData.setFieldMeta(dataTable, Fields.STAFF_META);
							break;
						case SINGLE_STUDENT:
							metaData.setFieldMeta(dataTable,
									Fields.STUDENT_META);
							break;
						case CSSREL:
							metaData.setFieldMeta(dataTable,
									Fields.RELATIONS_META);
							break;
						}
						MetaDataRepository.put(document.getType(), metaData);
						progressBar.setProgress(progress++,
								INITIAL_NUMBER_OF_REQUESTS);
						initComponentsOnComplete();
					}
				});
			}
		};
		VisualizationUtils.loadVisualizationApi(runnable, Table.PACKAGE);
	}

	private void initComponents() {
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {
				IndexPage indexPage = new IndexPage();
				indexPage.setEmail(email);
				indexPage.initComponents();
				RootPanel.get("outer-container").add(indexPage);
				History.addValueChangeHandler(indexPage);
			}

			@Override
			public void onFailure(Throwable reason) {

			}
		});
	}

	private void initComponentsOnComplete() {
		if (progress == INITIAL_NUMBER_OF_REQUESTS) {
			RootPanel.get().remove(progressBar);
			initComponents();
		}
	}

}
