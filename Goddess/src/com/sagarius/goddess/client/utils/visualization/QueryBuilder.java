package com.sagarius.goddess.client.utils.visualization;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.sagarius.goddess.client.utils.DocumentMetaData;
import com.sagarius.goddess.client.utils.DocumentMetaData.FieldMetaData;

public class QueryBuilder {
	public static enum OutputFormat {
		JSON_P, JSON, CSV, HTML;
		public String toQueryString() {
			switch (this) {
			case JSON_P:
				return "jsonp";
			case JSON:
				return "json";
			case CSV:
				return "csv";
			case HTML:
				return "html";
			}
			return null;
		}
	}

	public static enum Operator {
		CONTAINS, ENDS_WITH, EQ, GE, GT, LE, LIKE, LT, MATCHES, NE, STARTS_WITH;
		public String toQueryString() {
			switch (this) {
			case CONTAINS:
				return " contains ";
			case ENDS_WITH:
				return " ends with";
			case EQ:
				return " = ";
			case GE:
				return " >= ";
			case GT:
				return " > ";
			case LE:
				return " <= ";
			case LIKE:
				return " like ";
			case LT:
				return " < ";
			case MATCHES:
				return " matches ";
			case NE:
				return " != ";
			case STARTS_WITH:
				return " starts with ";
			default:
				return null;
			}
		}
	}

	private DocumentMetaData metaData;
	private String key;
	private int rowLimit = -1;
	private int offSet = -1;
	private List<String> selections = new LinkedList<String>();
	private List<String> filters = new LinkedList<String>();
	private OutputFormat outputFormat = OutputFormat.JSON_P;
	private String baseUrl;
	private String group;
	private String sheetName;

	public QueryBuilder addFilter(String fieldName, Operator operator,
			Object object) {
		FieldMetaData fieldMeta = metaData.getFieldMeta(fieldName);
		String filter = new StringBuilder(fieldMeta.getVizQueryId())
				.append(operator.toQueryString())
				.append(getQueryString(object, fieldMeta.getType())).toString();
		filters.add(filter);
		return this;
	}

	// TODO extend
	public static String getQueryString(Object object, ColumnType type) {
		switch (type) {
		case STRING:
			String output = object.toString();
			return "'" + output + "'";
		case BOOLEAN:
		case DATE:
		case DATETIME:
		case NUMBER:
		case TIMEOFDAY:
			return object.toString();
		}
		return null;
	}

	public QueryBuilder() {
	}

	public QueryBuilder(String baseUrl) {
		this.setBaseUrl(baseUrl);
	}

	public QueryBuilder removeFilters() {
		filters.clear();
		return this;
	}

	public QueryBuilder removeFilter(String filter) {
		filters.remove(filter);
		return this;
	}

	public QueryBuilder removeSelections() {
		selections.clear();
		return this;
	}

	public QueryBuilder removeSelection(String selection) {
		selections.remove(selection);
		return this;
	}

	public String get() {
		StringBuilder builder = new StringBuilder(getBaseUrl());
		builder.append("?key=").append(key);
		String queryString = getQuery();
		if (!queryString.equals("")) {
			builder.append("&tq=").append(URL.encode(queryString));
		}
		if (sheetName != null) {
			builder.append("&sheet=").append(sheetName);
		}
		builder.append("&headers=1");
		return builder.toString();
	}

	public String getQuery() {
		StringBuilder tqQuery = new StringBuilder();
		if (!selections.isEmpty()) {
			tqQuery.append("select ");
			for (String selection : selections) {
				tqQuery.append(selection).append(",");
			}
			tqQuery.deleteCharAt(tqQuery.length() - 1);
			tqQuery.append(" ");
		}
		if (!filters.isEmpty()) {
			tqQuery.append(" where ");
			for (String filter : filters) {
				tqQuery.append(filter + " and ");
			}
			int length = tqQuery.length();
			tqQuery.delete(length - 5, length);
			tqQuery.append(" ");
		}
		if (group != null) {
			tqQuery.append(" group by ").append(group);
		}
		if (rowLimit > -1) {
			tqQuery.append(" limit ").append(rowLimit);
		}
		if (offSet > -1) {
			tqQuery.append(" offset ").append(offSet);
		}
		if (outputFormat != OutputFormat.JSON_P) {
			tqQuery.append("&tqx=out:").append(outputFormat.toQueryString());
		}
		return tqQuery.toString();
	}

	public QueryBuilder setKey(String key) {
		this.key = key;
		return this;
	}

	public String getKey() {
		return key;
	}

	public QueryBuilder setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
		return this;
	}

	public int getRowLimit() {
		return rowLimit;
	}

	public QueryBuilder setOffSet(int offSet) {
		this.offSet = offSet;
		return this;
	}

	public int getOffSet() {
		return offSet;
	}

	public QueryBuilder addSelection(String selection, boolean isComplete) {
		if (isComplete) {
			selections.add(selection);
		} else {
			FieldMetaData fieldMeta = metaData.getFieldMeta(selection);
			selections.add(fieldMeta.getVizQueryId());
		}
		return this;
	}

	public QueryBuilder addSelection(String selection) {
		return addSelection(selection, false);
	}

	public QueryBuilder addSelections(List<String> selections) {
		for (String selection : selections) {
			FieldMetaData fieldMeta = metaData.getFieldMeta(selection);
			selections.add(fieldMeta.getVizQueryId());
		}
		return this;
	}

	public QueryBuilder addSelections(String... selections) {
		for (String selection : selections) {
			FieldMetaData fieldMeta = metaData.getFieldMeta(selection);
			this.selections.add(fieldMeta.getVizQueryId());
		}
		return this;
	}

	public QueryBuilder addFilter(String filter) {
		filters.add(filter);
		return this;
	}

	public QueryBuilder setMetaData(DocumentMetaData metaData) {
		this.metaData = metaData;
		this.key = metaData.getCccKey();
		return this;
	}

	public DocumentMetaData getMetaData() {
		return metaData;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public QueryBuilder setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public QueryBuilder setGroup(String group) {
		this.group = metaData.getFieldMeta(group).getVizQueryId();
		return this;
	}

	public String getGroup() {
		return group;
	}

	public QueryBuilder setSheetName(String sheetName) {
		this.sheetName = sheetName;
		return this;
	}

	public String getSheetName() {
		return sheetName;
	}
}
