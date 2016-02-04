package com.google.gwt.gdata.client.spreadsheet;

import com.google.gwt.gdata.client.DateTime;
import com.google.gwt.gdata.client.Query;

public class GDataQuery extends Query {
	protected GDataQuery() {
	}

	public static native GDataQuery newInstance(String feedUri)/*-{
		return new $wnd.google.gdata.client.Query(feedUri);
	}-*/;

	/**
	 * Returns the maximum number of results to be retrieved.
	 * 
	 * @return Maximum number of results to be retrieved.
	 */
	public final native double getMaxResults() /*-{
		return this.getMaxResults();
	}-*/;

	/**
	 * Sets the maximum number of results to be retrieved.
	 * 
	 * @param maxResults
	 *            Maximum number of results to be retrieved.
	 */
	public final void setMaxResults(double maxResults) {
		setNumericParam("max-results", maxResults);
	}

	/**
	 * Returns the 1-based index of the first result to be retrieved.
	 * 
	 * @return 1-based index of the first result to be retrieved.
	 */
	public final native double getStartIndex() /*-{
		return this.getStartIndex();
	}-*/;

	/**
	 * Sets the 1-based index of the first result to be retrieved. The default
	 * value is 1.
	 * 
	 * @param startIndex
	 *            1-based index of the first result to be retrieved.
	 */
	public final native void setStartIndex(double startIndex) /*-{
		this.setStartIndex(startIndex);
	}-*/;

	/**
	 * Returns the maximum updated date.
	 * 
	 * @return Maximum updated date.
	 */
	public final native DateTime getUpdatedMax() /*-{
		return this.getUpdatedMax();
	}-*/;

	/**
	 * Returns the minimum updated date.
	 * 
	 * @return Minimum updated date.
	 */
	public final native DateTime getUpdatedMin() /*-{
		return this.getUpdatedMin();
	}-*/;

	/**
	 * Sets the maximum updated date.
	 * 
	 * @param updatedMax
	 *            Maximum updated date.
	 */
	public final native void setUpdatedMax(DateTime updatedMax) /*-{
		this.setUpdatedMax(updatedMax);
	}-*/;

	public final void setUpdatedMin(String updatedMin) {
		setStringParam("updated-min", updatedMin);
	}

	/**
	 * Returns the full-text query string.
	 * 
	 * @return Full-text query string.
	 */
	public final native String getFullTextQuery() /*-{
		return this.getFullTextQuery();
	}-*/;

	/**
	 * Sets the full-text query string.
	 * 
	 * @param fullTextQuery
	 *            Full-text query string.
	 */
	public final native void setFullTextQuery(String fullTextQuery) /*-{
		this.setFullTextQuery(fullTextQuery);
	}-*/;
}
