package com.sagarius.goddess.client.utils.visualization;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DataTable extends com.google.gwt.visualization.client.DataTable {
	protected DataTable() {

	}

	public final int getIntValue(int rowIndex, int columnIndex) {
		try {
			return getValueInt(rowIndex, columnIndex);
		} catch (Exception e) {
			return 0;
		}
	}

	public final String getValue(int rowIndex, int columnIndex) {
		return getValue(rowIndex, columnIndex, true);
	}

	public final String getValue(int rowIndex, int columnIndex,
			boolean canBeDouble) {
		try {
			ColumnType type = getColumnType(columnIndex);
			switch (type) {
			case BOOLEAN:
				return getValueBoolean(rowIndex, columnIndex) + "";
			case DATE:
			case DATETIME:
				return DateTimeFormat.getMediumDateFormat().format(
						getValueDate(rowIndex, columnIndex));
			case NUMBER:
				if (canBeDouble) {
					return getValueDouble(rowIndex, columnIndex) + "";
				}
				return getValueInt(rowIndex, columnIndex) + "";
			case STRING:
				return getValueString(rowIndex, columnIndex);
			case TIMEOFDAY:
				return getValueTimeOfDay(rowIndex, columnIndex).toString();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
