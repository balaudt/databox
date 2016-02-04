package com.sagarius.goddess.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.CategoryStandardsContainer;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;

public class ClassesPresenter extends
		CategoryStandardsContainer<CategoryClassPresenter> implements
		CrumbHandler {

	@Override
	public void handleCrumb() {
		((Label) Breadcrumb.getCrumbAt(1, false)).setText("Classes");
		Breadcrumb.ensureMaxLength(2);
		com.sagarius.goddess.client.Utils.setActiveMainMenu("class");
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Classes");
		}
		HiddenRowButtons.clear();
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}

	@Override
	protected CategoryClassPresenter getStandardsInstance(String category) {
		return new CategoryClassPresenter(category);
	}

}
