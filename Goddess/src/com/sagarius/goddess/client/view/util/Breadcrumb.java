package com.sagarius.goddess.client.view.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Breadcrumb {

	public static final FlowPanel MAIN_PANEL = new FlowPanel();

	public static HTML getSeparator() {
		HTML html = new HTML("&nbsp;&gt;&nbsp;");
		html.setStyleName("fl");
		return html;
	}

	public static FlowPanel init() {
		Hyperlink homeLink = new Hyperlink("Home", "home");
		homeLink.setStyleName("fl");
		MAIN_PANEL.add(homeLink);
		return MAIN_PANEL;
	}

	public static int size() {
		return MAIN_PANEL.getWidgetCount() / 2 + 1;
	}

	public static Widget getCrumbAt(int index, boolean isAnchor) {
		try {
			Widget widget = MAIN_PANEL.getWidget(index * 2);
			if (isAnchor) {
				if (!(widget instanceof Hyperlink)) {
					remove(index);
					Hyperlink link = new Hyperlink();
					link.setStyleName("fl");
					MAIN_PANEL.insert(getSeparator(), index * 2 - 1);
					MAIN_PANEL.insert(link, index * 2);
					return link;
				}
				return widget;
			}
			if (!(widget instanceof Label)) {
				remove(index);
				Label label = new Label();
				label.setStyleName("fl");
				MAIN_PANEL.insert(getSeparator(), index * 2 - 1);
				MAIN_PANEL.insert(label, index * 2);
				return label;
			}
			return widget;
		} catch (IndexOutOfBoundsException e) {
			Widget widget = null;
			if (isAnchor) {
				widget = new Hyperlink();
			} else {
				widget = new Label();
				widget.getElement().setAttribute("style",
						"font-weight:bold;color:#333333;");
			}
			widget.setStyleName("fl");
			MAIN_PANEL.insert(getSeparator(), index * 2 - 1);
			MAIN_PANEL.insert(widget, index * 2);
			return widget;
		}
	}

	public static void remove(int index) {
		if (index == 0) {
			throw new RuntimeException("Shouldn't remove home link");
		}
		MAIN_PANEL.remove(index * 2);
		MAIN_PANEL.remove(index * 2 - 1);
	}

	public static void ensureMaxLength(int length) {
		int size = size();
		if (size > length) {
			for (int i = size - 1; i >= length; i--) {
				Breadcrumb.remove(i);
			}
		}
	}
}
