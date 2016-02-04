package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.view.settings.AcademicPage;
import com.sagarius.goddess.client.view.settings.AttendancePage;
import com.sagarius.goddess.client.view.settings.GeneralPage;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class SettingsPresenter extends TabPanel implements CrumbHandler {

	private AcademicPage academicPage;
	private GeneralPage generalPage;
	private AttendancePage attendancePage;

	public SettingsPresenter() {
		setStyleName("tabp-preset-1");
		generalPage = new GeneralPage();
		add(generalPage, "General");
		academicPage = new AcademicPage();
		add(academicPage, "Academic Year");
		attendancePage = new AttendancePage();
		add(attendancePage, "Attendance");
		selectTab(0);
		addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int visibleWidget = getDeckPanel().getVisibleWidget();
				BaseWidget widget = (BaseWidget) getWidget(visibleWidget);
				if (!widget.isLoaded()) {
					widget.refresh();
				}
			}
		});
	}

	@Override
	public void handleCrumb() {
		Label crumbLabel = (Label) Breadcrumb.getCrumbAt(1, false);
		crumbLabel.setText("Settings");
		Utils.setActiveMainMenu("settings");
		Breadcrumb.ensureMaxLength(2);
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int visibleWidget = getDeckPanel().getVisibleWidget();
				switch (visibleWidget) {
				case 0:
					generalPage.refresh();
					break;
				case 1:
					academicPage.refresh();
					break;
				case 2:
					attendancePage.refresh();
					break;
				}
			}
		});
	}

}
