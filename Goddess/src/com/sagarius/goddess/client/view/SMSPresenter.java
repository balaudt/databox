package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.view.util.SentMessages;
import com.sagarius.goddess.client.view.util.TemplateViewer;

public class SMSPresenter extends TabPanel implements CrumbHandler {
	private SentMessages messages = new SentMessages();
	private TemplateViewer viewer = new TemplateViewer();

	public SMSPresenter() {
		setStyleName("tabp-preset-1");
		add(messages, "Sent Messages");
		add(viewer, "Templates");
		messages.init();
		selectTab(0);
		addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (getDeckPanel().getVisibleWidget() == 1) {
					if(!viewer.isLoaded()){
						viewer.init();
					}
				}
			}
		});
	}

	@Override
	public void handleCrumb() {
		((Label) Breadcrumb.getCrumbAt(1, false)).setText("SMS");
		Breadcrumb.ensureMaxLength(2);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - SMS");
		}
		com.sagarius.goddess.client.Utils.setActiveMainMenu("sms");
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				messages.init();
				viewer.init();
			}
		});
	}

}
