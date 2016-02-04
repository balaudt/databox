package org.sagarius.radix.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

public class Table extends FlexTable {
	private Element headRowElement;

	public static enum SortType {
		ALPHA_NUMERIC("alphanumeric"), NUMERIC("numeric"), NUMERIC_COMMA(
				"numeric_comma"), IGONRE_CASE("ignorecase"), CURRENCY(
				"currency"), CURRENCY_COMMA("currency_comma"), DATE("date");
		private String clazz;

		private SortType(String classString) {
			clazz = classString;
		}

		public String getClazz() {
			return clazz;
		}
	}

	public Table() {
		super();
		Element headElement = DOM.createTHead();
		headRowElement = DOM.createTR();
		headRowElement.setAttribute("style", "text-align: center");
		headElement.appendChild(headRowElement);
		Element thisElement = getElement();
		thisElement.insertFirst(headElement);
		thisElement.setClassName("table-autosort");
	}

	public void addHeader(String text, SortType type) {
		Element thElement = DOM.createTH();
		thElement.setInnerHTML(text);
		if (type != null) {
			String clazz = type.getClazz();
			thElement.setClassName("table-sortable:" + clazz
					+ " table-sortable");
		}
		headRowElement.appendChild(thElement);
	}

	public native void initialize()/*-{
		//		$wnd.Table.probe(this.@org.sagarius.radix.client.view.Table::getElement());
			$wnd.Table.auto();
	}-*/;
}
