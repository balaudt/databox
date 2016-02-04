package com.sagarius.goddess.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Query;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.utils.Fields.Generic;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.client.utils.Fields.Student;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder;
import com.sagarius.goddess.client.utils.visualization.QueryBuilder.Operator;

public class SearchSuggestOracle extends SuggestOracle {

	private MemberType type;

	private QueryBuilder builder;

	/**
	 * Class to hold a response from the server.
	 */
	private static class ServerResponse {

		/**
		 * Request made by the SuggestBox.
		 */
		private final Request request;

		/**
		 * The number of suggestions the server was asked for
		 */
		private final int serverSuggestionsLimit;

		/**
		 * Suggestions returned by the server in response to the request.
		 */
		private final List<? extends Suggestion> suggestions;

		/**
		 * Create a new instance.
		 * 
		 * @param request
		 *            Request from the SuggestBox.
		 * @param serverSuggestionsLimit
		 *            The number of suggestions we asked the server for.
		 * @param suggestions
		 *            The suggestions returned by the server.
		 */
		private ServerResponse(Request request, int serverSuggestionsLimit,
				List<? extends Suggestion> suggestions) {
			this.request = request;
			this.serverSuggestionsLimit = serverSuggestionsLimit;
			this.suggestions = suggestions;
		}

		/**
		 * Get the query string that was sent to the server.
		 * 
		 * @return The query.
		 */
		private String getQuery() {
			return request.getQuery();
		}

		/**
		 * Does the response include all possible suggestions for the query.
		 * 
		 * @return True or false.
		 */
		private boolean isComplete() {
			if(suggestions==null){
				return true;
			}
			return suggestions.size() <= serverSuggestionsLimit;
		}

		/**
		 * Filter the suggestions we got back from the server.
		 * 
		 * @param query
		 *            The query string.
		 * @param limit
		 *            The number of suggestions to return.
		 * @return The suggestions.
		 */
		public List<Suggestion> filter(String query, int limit) {
			if(suggestions==null){
				return Arrays.asList(NO_RESULT_SUGGESTION);
			}
			List<Suggestion> newSuggestions = new ArrayList<Suggestion>(limit);
			int i = 0, s = suggestions.size();
			while (i < s
					&& !suggestions.get(i).getDisplayString().contains(query)) {
				++i;
			}
			while (i < s && newSuggestions.size() < limit
					&& suggestions.get(i).getDisplayString().contains(query)) {
				newSuggestions.add(suggestions.get(i));
				++i;
			}
			return newSuggestions;
		}
	}

	/**
	 * Number of suggestions to request from the server. Using 75 lets you test
	 * the logic that uses the isComplete method. Try using "al" as the initial
	 * query
	 */
	// private static final int numberOfServerSuggestions = 100;
	private static final int numberOfServerSuggestions = 75;

	/**
	 * Is there a request in progress
	 */
	private boolean requestInProgress = false;

	/**
	 * The most recent request made by the client.
	 */
	private Request mostRecentClientRequest = null;

	/**
	 * The most recent response from the server.
	 */
	private ServerResponse mostRecentServerResponse = null;

	public SearchSuggestOracle() {
		builder = new QueryBuilder("https://spreadsheets.google.com/tq")
				.setKey("0AlKVPWzSYPMCdFUzaUNyMTF1RGhyUW9sc3lJcS1pa0E")
				.addSelection("D", true).addSelection("F", true)
				.setRowLimit(75);
	}

	@Override
	public void requestSuggestions(Request request, Callback callback) {
		// Record this request as the most recent one.
		mostRecentClientRequest = request;
		// If there is not currently a request in progress return some
		// suggestions. If there is a request in progress
		// suggestions will be returned when it completes.
		if (!requestInProgress) {
			returnSuggestions(callback);
		}
	}

	private void makeRequest(final Request request, final Callback callback) {
		requestInProgress = true;
		callback.onSuggestionsReady(request,
				new Response(Arrays.asList(new Suggestion[] { new Suggestion() {

					@Override
					public String getDisplayString() {
						return "Loading...";
					}

					@Override
					public String getReplacementString() {
						return "Loading...";
					}
				} })));
		switch (type) {
		case STUDENT:
			MetaDataRepository.get(DocumentType.SINGLE_STUDENT,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result)
									.addSelections(Generic.NAME,
											Student.STUDENT_ID,
											Generic.STANDARD, Generic.SECTION)
									.addFilter(Generic.NAME, Operator.CONTAINS,
											request.getQuery());
							onTypeLoad(request, callback, type);
						}

						@Override
						public void onFailure(Throwable caught) {
							requestInProgress = false;
							callback.onSuggestionsReady(
									mostRecentClientRequest,
									new Response(Collections
											.<Suggestion> emptyList()));
						}
					});
			break;
		case STAFF:
			MetaDataRepository.get(DocumentType.STAFF,
					new AsyncCallback<DocumentMetaData>() {

						@Override
						public void onSuccess(DocumentMetaData result) {
							builder = new QueryBuilder(Utils.sheetVizUrl)
									.setMetaData(result)
									.addSelections(Generic.NAME, Staff.STAFF_ID)
									.addFilter(Generic.NAME, Operator.CONTAINS,
											request.getQuery());
							onTypeLoad(request, callback, type);
						}

						@Override
						public void onFailure(Throwable caught) {
							requestInProgress = false;
							callback.onSuggestionsReady(
									mostRecentClientRequest,
									new Response(Collections
											.<Suggestion> emptyList()));
						}
					});

		}
	}

	private static final Suggestion NO_RESULT_SUGGESTION = new Suggestion() {

		@Override
		public String getReplacementString() {
			return "";
		}

		@Override
		public String getDisplayString() {
			return "No results";
		}
	};

	private void onTypeLoad(final Request request, final Callback callback,
			final MemberType type) {
		Query query = Query.create(builder.get());
		query.send(new Query.Callback() {

			@Override
			public void onResponse(QueryResponse response) {
				requestInProgress = false;
				DataTable table = response.getDataTable();
				int rows = table.getNumberOfRows();
				if (rows == 0) {
					mostRecentServerResponse = new ServerResponse(request,
							numberOfServerSuggestions, null);
					returnSuggestions(callback);
					return;
				}
				List<SearchSuggestion> suggestions = new ArrayList<SearchSuggestion>();
				for (int i = 0; i < rows; i++) {
					switch (type) {
					case STAFF:
						suggestions.add(new SearchSuggestion(table
								.getValueString(i, 0), table.getValueString(i,
								1), type));
						break;
					case STUDENT:
						suggestions.add(new SearchSuggestion(table
								.getValueString(i, 0)
								+ " ,"
								+ table.getValueString(i, 2)
								+ "-"
								+ table.getValueString(i, 3), table
								.getValueString(i, 1), type));
					}
				}
				mostRecentServerResponse = new ServerResponse(request,
						numberOfServerSuggestions, suggestions);
				returnSuggestions(callback);
			}
		});
	}

	/**
	 * Return some suggestions to the SuggestBox. At this point we know that
	 * there is no call to the server currently in progress and we try to
	 * satisfy the request from the most recent results from the server before
	 * we call the server.
	 * 
	 * @param callback
	 *            The callback.
	 */
	private void returnSuggestions(Callback callback) {
		// For single character queries return an empty list.
		final String mostRecentQuery = mostRecentClientRequest.getQuery();
		if (mostRecentQuery.length() == 1) {
			callback.onSuggestionsReady(mostRecentClientRequest, new Response(
					Collections.<Suggestion> emptyList()));
			return;
		}
		// If we have a response from the server, and it includes all the
		// possible suggestions for its request, and
		// that request is a superset of the request we're trying to satisfy now
		// then use the server results, otherwise
		// ask the server for some suggestions.
		if (mostRecentServerResponse != null) {
			if (mostRecentQuery.equals(mostRecentServerResponse.getQuery())) {
				Response resp = new Response(mostRecentServerResponse.filter(
						mostRecentClientRequest.getQuery(),
						mostRecentClientRequest.getLimit()));

				callback.onSuggestionsReady(mostRecentClientRequest, resp);
			} else if (mostRecentServerResponse.isComplete()
					&& mostRecentQuery.startsWith(mostRecentServerResponse
							.getQuery())) {
				// Response resp = new Response(mostRecentServerResponse.filter(
				// mostRecentClientRequest.getQuery(),
				// mostRecentClientRequest.getLimit()));
				// callback.onSuggestionsReady(mostRecentClientRequest, resp);
				makeRequest(mostRecentClientRequest, callback);
			} else {
				makeRequest(mostRecentClientRequest, callback);
			}
		} else {
			makeRequest(mostRecentClientRequest, callback);
		}
	}

	public void setType(int type) {
		switch (type) {
		case 1:
			this.type = MemberType.STUDENT;
			break;
		case 2:
			this.type = MemberType.STAFF;
		}
	}

	public MemberType getType() {
		return type;
	}

}
