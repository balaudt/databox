package com.sagarius.goddess.client.view.util;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HiddenRowButtons {
	public static FlowPanel mainPanel;
	private static HandlerRegistration refreshHandler;
	private static Anchor refreshButton;

	public static Anchor getRefreshButton() {
		return refreshButton;
	}

	public static HTML getSeparator() {
		HTML html = new HTML("&nbsp;&gt;&nbsp;");
		html.setStyleName("fl");
		return html;
	}

	public static void init(final FlowPanel searcher, FlowPanel inPanel) {
		// Hide search box if necessary options
		inPanel.getElement().setId("search-hide");
		final Anchor hideSearch = new Anchor("[+] Show Search", "javascript:");
		hideSearch.setStyleName("fl");
		hideSearch.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (searcher.isVisible()) {
					searcher.setVisible(false);
					hideSearch.setText("[+] Show search");
				} else {
					searcher.setVisible(true);
					hideSearch.setText("[-] Hide search");
				}
			}
		});
		mainPanel = new FlowPanel();
		mainPanel.setStyleName("fl");
		inPanel.add(mainPanel);
		refreshButton = new Anchor("[~] Refresh");
		refreshButton.setStyleName("fl");
		inPanel.add(refreshButton);
		inPanel.add(hideSearch);
		searcher.setVisible(false);
	}

	public static void setRefreshHandler(ClickHandler handler) {
		if (refreshHandler != null) {
			refreshHandler.removeHandler();
		}
		refreshHandler = refreshButton.addClickHandler(handler);
	}

	public static void clear() {
		mainPanel.clear();
	}

	public static void add(Widget widget) {
		widget.addStyleName("fl");
		mainPanel.add(widget);
	}
}
