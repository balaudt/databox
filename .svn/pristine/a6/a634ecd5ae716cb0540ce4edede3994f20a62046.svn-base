package com.sagarius.goddess.server.util;

import java.net.URL;

import org.apache.commons.lang.StringUtils;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.visualization.datasource.query.ColumnSort;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.query.SortOrder;
import com.google.visualization.datasource.query.parser.QueryParser;

public class VisualizationUtils {
	public static ListQuery getListQuery(String tqQuery, URL listFeedUrl) {
		ListQuery listQuery = new ListQuery(listFeedUrl);
		Query query = null;
		if (StringUtils.isNotBlank(tqQuery)) {
			try {
				query = QueryParser.parseString(tqQuery);
				if (query.getFilter() != null) {
					listQuery.setSpreadsheetQuery(query.getFilter()
							.toQueryString().replaceAll("`", ""));
				}
				if (query.getRowLimit() != -1) {
					listQuery.setMaxResults(query.getRowLimit());
				}
				if (query.getRowOffset() > 0) {
					listQuery.setStartIndex(query.getRowOffset());
				}
				if (query.getSort() != null) {
					ColumnSort columnSort = query.getSort().getSortColumns()
							.get(0);
					listQuery.setSortColumn(columnSort.getColumn().getId());
					listQuery.setReverse(columnSort.getOrder().equals(
							SortOrder.DESCENDING));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return listQuery;
	}

}
