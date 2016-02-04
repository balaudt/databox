package com.sagarius.goddess.client.ajax;

import org.restlet.client.Request;

import com.google.gwt.gdata.client.spreadsheet.CellQuery;
import com.google.gwt.gdata.client.spreadsheet.ListQuery;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gwt.gdata.client.spreadsheet.WorksheetQuery;
import com.sagarius.goddess.client.model.enumerations.DocumentType;

public class GenericRequest {
	@Override
	public String toString() {
		return "GenericRequest [type=" + type + ", httpRequest=" + httpRequest
				+ ", visualizationQuery=" + visualizationQuery
				+ ", spreadsheetQuery=" + spreadsheetQuery
				+ ", worksheetQuery=" + worksheetQuery + ", listQuery="
				+ listQuery + ", cellQuery=" + cellQuery + ", typeQuery="
				+ typeQuery + ", keyQuery=" + keyQuery + ", cacheRequest="
				+ cacheRequest + "]";
	}

	private RequestType type;
	private Request httpRequest;
	private String visualizationQuery;
	private SpreadsheetQuery spreadsheetQuery;
	private WorksheetQuery worksheetQuery;
	private ListQuery listQuery;
	private CellQuery cellQuery;
	private DocumentType typeQuery;
	private String keyQuery;
	private CacheRequest cacheRequest;

	public RequestType getType() {
		return type;
	}

	public Request getHttpRequest() {
		return httpRequest;
	}

	public String getVisualizationQuery() {
		return visualizationQuery;
	}

	public SpreadsheetQuery getSpreadsheetQuery() {
		return spreadsheetQuery;
	}

	public WorksheetQuery getWorksheetQuery() {
		return worksheetQuery;
	}

	public ListQuery getListQuery() {
		return listQuery;
	}

	public CellQuery getCellQuery() {
		return cellQuery;
	}

	public GenericRequest() {
	}

	public GenericRequest setType(RequestType type) {
		this.type = type;
		return this;
	}

	public GenericRequest setHttpRequest(Request httpRequest) {
		this.httpRequest = httpRequest;
		return this;
	}

	public GenericRequest setVisualizationQuery(String visualizationQuery) {
		this.visualizationQuery = visualizationQuery;
		return this;
	}

	public GenericRequest setSpreadsheetQuery(SpreadsheetQuery spreadsheetQuery) {
		this.spreadsheetQuery = spreadsheetQuery;
		return this;
	}

	public GenericRequest setWorksheetQuery(WorksheetQuery worksheetQuery) {
		this.worksheetQuery = worksheetQuery;
		return this;
	}

	public GenericRequest setListQuery(ListQuery listQuery) {
		this.listQuery = listQuery;
		return this;
	}

	public GenericRequest setCellQuery(CellQuery cellQuery) {
		this.cellQuery = cellQuery;
		return this;
	}

	public DocumentType getTypeQuery() {
		return typeQuery;
	}

	public GenericRequest setTypeQuery(DocumentType typeQuery) {
		this.typeQuery = typeQuery;
		this.keyQuery = null;
		return this;
	}

	public String getKeyQuery() {
		return keyQuery;
	}

	public GenericRequest setKeyQuery(String keyQuery) {
		this.typeQuery = null;
		this.keyQuery = keyQuery;
		return this;
	}

	public GenericRequest setCacheRequest(CacheRequest cacheRequest) {
		this.cacheRequest = cacheRequest;
		return this;
	}

	public CacheRequest getCacheRequest() {
		return cacheRequest;
	}

}
