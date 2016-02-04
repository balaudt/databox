package com.sagarius.goddess.client.view.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Widget;

public class ControlPanel extends FlowPanel {
	public ControlPanel() {
		this(false);
	}

	public ControlPanel(boolean doClear) {
		setStyleName("related-links");
		if (doClear) {
			addStyleName("clear");
		}
	}

	public ControlPanel add(Widget widget, String clazz) {
		widget.setStyleName("mr4");
		widget.addStyleName(clazz);
		add(widget);
		return this;
	}

	public void add(Widget widget) {
		widget.addStyleName("mr4");
		super.add(widget);
	}

	public Widget getControlAt(int index) {
		return getWidget(index);
	}

	public void setEnabled(boolean isEnabled) {
		int count = getWidgetCount();
		for (int i = 0; i < count; i++) {
			FocusWidget widget = (FocusWidget) getWidget(i);
			widget.setEnabled(isEnabled);
		}
	}
}