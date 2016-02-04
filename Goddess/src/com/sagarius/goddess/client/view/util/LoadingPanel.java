package com.sagarius.goddess.client.view.util;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

public class LoadingPanel extends PopupPanel {
	public static final LoadingPanel INSTANCE = new LoadingPanel();
	private HTML messageHtml;

	private LoadingPanel() {
		FlexTable table = new FlexTable();
		table.setWidget(0, 0, new Image("/goddess/images/gadget-load.gif"));
		messageHtml = new HTML();
		table.setWidget(1, 0, messageHtml);
		setWidget(table);
		setModal(true);
		setAutoHideEnabled(false);
	}

	public LoadingPanel setMessage(String html) {
		messageHtml.setHTML(html);
		return this;
	}
}
