package com.sagarius.goddess.client.ajax;

import org.restlet.client.Response;

import com.google.gwt.gdata.client.spreadsheet.CellFeed;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetFeed;
import com.google.gwt.gdata.client.spreadsheet.WorksheetFeed;
import com.google.gwt.visualization.client.QueryResponse;
import com.sagarius.goddess.client.utils.DocumentMetaData;

public class GenericResponse {
	@Override
	public String toString() {
		return "GenericResponse [type=" + type + ", response=" + response
				+ ", queryResponse=" + queryResponse + ", spreadsheetFeed="
				+ spreadsheetFeed + ", worksheetFeed=" + worksheetFeed
				+ ", listFeed=" + listFeed + ", cellFeed=" + cellFeed
				+ ", metaData=" + metaData + ", output=" + output + "]";
	}

	private RequestType type;
	private Response response;
	private QueryResponse queryResponse;
	private SpreadsheetFeed spreadsheetFeed;
	private WorksheetFeed worksheetFeed;
	private ListFeed listFeed;
	private CellFeed cellFeed;
	private DocumentMetaData metaData;
	private Object output;

	public RequestType getType() {
		return type;
	}

	public GenericResponse setType(RequestType type) {
		this.type = type;
		return this;
	}

	public Response getResponse() {
		return response;
	}

	public GenericResponse setResponse(Response response) {
		this.response = response;
		return this;
	}

	public QueryResponse getQueryResponse() {
		return queryResponse;
	}

	public GenericResponse setQueryResponse(QueryResponse queryResponse) {
		this.queryResponse = queryResponse;
		return this;
	}

	public SpreadsheetFeed getSpreadsheetFeed() {
		return spreadsheetFeed;
	}

	public GenericResponse setSpreadsheetFeed(SpreadsheetFeed spreadsheetFeed) {
		this.spreadsheetFeed = spreadsheetFeed;
		return this;
	}

	public WorksheetFeed getWorksheetFeed() {
		return worksheetFeed;
	}

	public GenericResponse setWorksheetFeed(WorksheetFeed worksheetFeed) {
		this.worksheetFeed = worksheetFeed;
		return this;
	}

	public ListFeed getListFeed() {
		return listFeed;
	}

	public GenericResponse setListFeed(ListFeed listFeed) {
		this.listFeed = listFeed;
		return this;
	}

	public CellFeed getCellFeed() {
		return cellFeed;
	}

	public GenericResponse setCellFeed(CellFeed cellFeed) {
		this.cellFeed = cellFeed;
		return this;
	}

	public GenericResponse setMetaData(DocumentMetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	public DocumentMetaData getMetaData() {
		return metaData;
	}

	public GenericResponse setOutput(Object output) {
		this.output = output;
		return this;
	}

	public Object getOutput() {
		return output;
	}

}
