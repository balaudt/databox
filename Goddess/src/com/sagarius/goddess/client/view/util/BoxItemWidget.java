package com.sagarius.goddess.client.view.util;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BoxItemWidget extends FlowPanel {
	private FlowPanel anchorContainer;
	private FlowPanel boxPanel;

	public BoxItemWidget(int span, String iconClass, String title,
			boolean doClear, boolean isLast) {
		this(span, iconClass, new Label(title), doClear, isLast);
	}

	public BoxItemWidget(int span, String iconClass, String title,
			boolean doClear) {
		this(span, iconClass, new Label(title), doClear);
	}

	public BoxItemWidget(int span, String iconClass, String title) {
		this(span, iconClass, new Label(title));
	}

	public BoxItemWidget(int span, String iconClass, Widget title) {
		this(span, iconClass, title, false);
	}

	public BoxItemWidget(int span, String iconClass, Widget title,
			boolean doClear) {
		this(span, iconClass, title, doClear, false);
	}

	public BoxItemWidget(int span, String iconClass, Widget title,
			boolean doClear, boolean isLast) {
		setStyleName("mb3 span-" + span);
		if (doClear) {
			addStyleName("clear");
		}
		if (isLast) {
			addStyleName("last");
		}
		add(boxPanel = new FlowPanel());
		boxPanel.setStyleName("box-preset-1 " + iconClass);

		title.setStyleName("title strong");
		boxPanel.add(title);

		anchorContainer = new FlowPanel();
		anchorContainer.setStyleName("controls clearfix");
		boxPanel.add(anchorContainer);
	}

	public BoxItemWidget addControl(Anchor anchor) {
		anchor.setStyleName("fl");
		if (anchorContainer.getWidgetCount() > 0) {
			HTML spacer = new HTML("&nbsp;&nbsp;");
			spacer.setStyleName("fl");
			anchorContainer.add(spacer);
		}
		anchorContainer.add(anchor);
		return this;
	}

	public BoxItemWidget addControl(Hyperlink hyperlink) {
		hyperlink.setStyleName("fl");
		if (anchorContainer.getWidgetCount() > 0) {
			HTML spacer = new HTML("&nbsp;&nbsp;");
			spacer.setStyleName("fl");
			anchorContainer.add(spacer);
		}
		anchorContainer.add(hyperlink);
		return this;
	}

	public BoxItemWidget addControl(Label label) {
		label.setStyleName("fl");
		if (anchorContainer.getWidgetCount() > 0) {
			HTML spacer = new HTML("&nbsp;&nbsp;");
			spacer.setStyleName("fl");
			anchorContainer.add(spacer);
		}
		anchorContainer.add(label);
		return this;
	}
}
