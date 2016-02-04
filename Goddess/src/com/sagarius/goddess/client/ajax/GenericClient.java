package com.sagarius.goddess.client.ajax;

import java.io.IOException;

import org.restlet.client.Client;
import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Protocol;
import org.restlet.client.data.Status;

import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.spreadsheet.CellFeed;
import com.google.gwt.gdata.client.spreadsheet.CellFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.ListFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetFeed;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gwt.gdata.client.spreadsheet.WorksheetFeed;
import com.google.gwt.gdata.client.spreadsheet.WorksheetFeedCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.Query.Callback;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.MetaDataRepository;

public class GenericClient {
	private GenericResponse currentResponse;
	private GenericRequest currentRequest;
	private String lastUpdated;

	public boolean isModified() {
		if (currentRequest == null || currentResponse == null) {
			return true;
		}
		String updated = null;
		switch (currentRequest.getType()) {
		case NONE:
			return true;
		case HTTP:
			if (currentResponse.getResponse().getStatus() == Status.REDIRECTION_NOT_MODIFIED) {
				return false;
			}
			break;
		case GDATA_SPREADSHEET:
			if (lastUpdated == null) {
				return true;
			}
			updated = currentResponse.getSpreadsheetFeed().getLastUpdated();
			if (updated.equals(lastUpdated)) {
				return false;
			}
			break;
		case GDATA_WORKSHEET:
			if (lastUpdated == null) {
				return true;
			}
			updated = currentResponse.getWorksheetFeed().getLastUpdated();
			if (updated.equals(lastUpdated)) {
				return false;
			}
			break;
		case GDATA_LIST:
			if (lastUpdated == null) {
				return true;
			}
			updated = currentResponse.getListFeed().getLastUpdated();
			if (updated.equals(lastUpdated)) {
				return false;
			}
			break;
		case GDATA_CELL:
			if (lastUpdated == null) {
				return true;
			}
			updated = currentResponse.getCellFeed().getLastUpdated();
			if (updated.equals(lastUpdated)) {
				return false;
			}
			break;
		case DOCUMENT_META:
			return false;
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handle(GenericRequest request,
			final AsyncCallback<GenericResponse> callback) {
		currentRequest = request;
		final RequestType type = request.getType();
		SpreadsheetService service = SpreadsheetService.newInstance("Goddess");
		switch (type) {
		case NONE:
			callback.onSuccess(null);
			break;
		case HTTP:
			Client client = new Client(Protocol.HTTP);
			client.handle(request.getHttpRequest(), new Uniform() {

				@Override
				public void handle(Request request, Response response) {
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
					callback.onSuccess(currentResponse = new GenericResponse()
							.setType(type).setResponse(response));
				}
			});
			break;
		case VISUALIZATION:
			Query query = Query.create(request.getVisualizationQuery());
			query.send(new Callback() {

				@Override
				public void onResponse(QueryResponse response) {
					if (response.isError()) {
						callback.onFailure(new Throwable(response
								.getDetailedMessage()));
						return;
					}
					callback.onSuccess(currentResponse = new GenericResponse()
							.setType(type).setQueryResponse(response));
				}
			});
			break;
		case GDATA_SPREADSHEET:
			service.getSpreadsheetFeed(request.getSpreadsheetQuery(),
					new SpreadsheetFeedCallback() {

						@Override
						public void onSuccess(SpreadsheetFeed result) {
							lastUpdated = result.getLastUpdated();
							callback.onSuccess(currentResponse = new GenericResponse()
									.setType(type).setSpreadsheetFeed(result));
						}

						@Override
						public void onFailure(CallErrorException caught) {
							callback.onFailure(caught);
						}
					});
			break;
		case GDATA_WORKSHEET:
			service.getWorksheetFeed(request.getWorksheetQuery(),
					new WorksheetFeedCallback() {

						@Override
						public void onSuccess(WorksheetFeed result) {
							callback.onSuccess(currentResponse = new GenericResponse()
									.setType(type).setWorksheetFeed(result));
						}

						@Override
						public void onFailure(CallErrorException caught) {
							callback.onFailure(caught);
						}
					});
			break;
		case GDATA_LIST:
			service.getListFeed(request.getListQuery(), new ListFeedCallback() {

				@Override
				public void onSuccess(ListFeed result) {
					callback.onSuccess(currentResponse = new GenericResponse()
							.setType(type).setListFeed(result));
				}

				@Override
				public void onFailure(CallErrorException caught) {
					callback.onFailure(caught);
				}
			});
			break;
		case GDATA_CELL:
			service.getCellFeed(request.getCellQuery(), new CellFeedCallback() {

				@Override
				public void onSuccess(CellFeed result) {
					callback.onSuccess(currentResponse = new GenericResponse()
							.setType(type).setCellFeed(result));
				}

				@Override
				public void onFailure(CallErrorException caught) {
					callback.onFailure(caught);
				}
			});
			break;
		case DOCUMENT_META:
			if (request.getKeyQuery() != null) {
				MetaDataRepository.get(request.getKeyQuery(),
						new AsyncCallback<DocumentMetaData>() {

							@Override
							public void onSuccess(DocumentMetaData result) {
								callback.onSuccess(currentResponse = new GenericResponse()
										.setType(type).setMetaData(result));
							}

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}
						});
			} else {
				MetaDataRepository.get(request.getTypeQuery(),
						new AsyncCallback<DocumentMetaData>() {

							@Override
							public void onSuccess(DocumentMetaData result) {
								callback.onSuccess(currentResponse = new GenericResponse()
										.setType(type).setMetaData(result));
							}

							@Override
							public void onFailure(Throwable caught) {
								callback.onFailure(caught);
							}
						});
			}
			break;
		case CACHE:
			CacheHandler handler = Cacher.get(request.getCacheRequest()
					.getType());
			handler.handle(request.getCacheRequest().getInput(),
					new AsyncCallback<Object>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Object result) {
							callback.onSuccess(currentResponse = new GenericResponse()
									.setType(RequestType.CACHE).setOutput(
											result));
						}
					});
		}
	}
}
