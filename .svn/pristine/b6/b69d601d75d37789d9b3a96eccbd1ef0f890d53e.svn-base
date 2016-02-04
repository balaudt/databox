package com.sagarius.goddess.client.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;

public class DocumentMetaData {
	private String cccKey;
	private String spreadsheetKey;
	private String displayName;
	private Map<String, FieldMetaData> metas;

	public FieldMetaData getFieldMeta(String fieldName) {
		return metas.get(fieldName);
	}

	public void setFieldMeta(DataTable table, String... mandateFields) {
		metas = new HashMap<String, DocumentMetaData.FieldMetaData>();
		List<String> fields = Arrays.asList(mandateFields);
		int columns = table.getNumberOfColumns();
		for (int i = 0; i < columns; i++) {
			// TODO Replace all special characters. Is necessary or enforce
			// conditions for no use of special characters in column headers
			String columnId = table.getColumnLabel(i).toLowerCase()
					.replaceAll(" ", "").replaceAll("_", "")
					.replaceAll("'", "");
			if (fields != null && !fields.contains(columnId)) {
				continue;
			}
			metas.put(columnId, new FieldMetaData(table.getColumnType(i), i,
					table.getColumnId(i)));
		}
	}

	public static class FieldMetaData {
		private ColumnType type;
		private int index;
		private String vizQueryId;

		public FieldMetaData(ColumnType type, int index, String vizQueryId) {
			super();
			this.type = type;
			this.index = index;
			this.vizQueryId = vizQueryId;
		}

		public ColumnType getType() {
			return type;
		}

		public void setType(ColumnType type) {
			this.type = type;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getVizQueryId() {
			return vizQueryId;
		}

		public void setVizQueryId(String vizQueryId) {
			this.vizQueryId = vizQueryId;
		}
	}

	public void setCccKey(String cccKey) {
		this.cccKey = cccKey;
	}

	public String getCccKey() {
		return cccKey;
	}

	public void setSpreadsheetKey(String spreadsheetKey) {
		this.spreadsheetKey = spreadsheetKey;
	}

	public String getSpreadsheetKey() {
		return spreadsheetKey;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
