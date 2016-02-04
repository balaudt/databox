package com.sagarius.goddess.client.view.settings;

import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public abstract class BasePage extends FlowPanel {
	protected FormPanel viewPanel;
	protected com.google.gwt.user.client.ui.FormPanel editFormPanel;
	protected FormPanel editPanel;
	private DeckPanel leftPanel;

	public BasePage() {
		leftPanel = new DeckPanel();
		leftPanel.add(viewPanel = new FormPanel());
		viewPanel.getElement().setAttribute("style", "height:auto;");
		leftPanel
				.add(editFormPanel = new com.google.gwt.user.client.ui.FormPanel());
		editPanel = new FormPanel();
		editFormPanel.setWidget(editPanel);
		editPanel.getElement().setAttribute("style", "height:auto;");
		leftPanel.showWidget(0);
		add(leftPanel);

		FlowPanel helpPanel = new FlowPanel();
		helpPanel.setStyleName("local-help fr pt40 pb5");
		HTML helpHtml = new HTML(getHelp());
		helpHtml.setStyleName("box-preset-2");
		helpPanel.add(helpHtml);
		add(helpPanel);
	}

	public abstract String getHelp();

	public void setState(boolean state) {
		if (state) {
			leftPanel.showWidget(1);
		} else {
			leftPanel.showWidget(0);
		}
	}
}
