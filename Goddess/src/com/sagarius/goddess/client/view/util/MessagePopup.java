package com.sagarius.goddess.client.view.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class MessagePopup extends PopupPanel {
	public static final MessagePopup INSTANCE = new MessagePopup();
	private HTML messageHtml;

	public MessagePopup() {
		FlexTable table = new FlexTable();
		messageHtml = new HTML();
		table.setWidget(0, 0, messageHtml);
		table.setWidget(1, 0, new Button("Ok", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		}));
		setWidget(table);
		setModal(true);
		setAutoHideEnabled(false);
	}

	public MessagePopup setMessage(String html) {
		messageHtml.setHTML(html);
		return this;
	}
}
