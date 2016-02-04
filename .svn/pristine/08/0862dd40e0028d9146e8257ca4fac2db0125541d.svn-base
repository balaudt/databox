package com.sagarius.goddess.client.view.parent;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.Singletons;

public class StudentsPresenter extends Composite implements CrumbHandler {
	private TabPanel studentSwitcher = new TabPanel();

	public StudentsPresenter() {
		studentSwitcher.setStyleName("tabp-preset-1");
		Singletons.getClient().handle(
				new Request(Method.GET, "/parent/student"), new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						if (response.getStatus().isError()) {
							return;
						}
						try {
							JSONArray input = (JSONArray) JSONParser
									.parse(response.getEntity().getText());
							int size = input.size();
							for (int i = 0; i < size; i++) {
								String[] studValues = ((JSONString) input
										.get(i)).stringValue().split("-");
								studentSwitcher.add(new StudentPresenter(
										studValues[1], studValues[0]),
										studValues[0]);
							}
							studentSwitcher.selectTab(0);
							load(0);
							studentSwitcher
									.addSelectionHandler(new SelectionHandler<Integer>() {

										@Override
										public void onSelection(
												SelectionEvent<Integer> event) {
											load(event.getSelectedItem());
										}
									});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
		initWidget(studentSwitcher);
	}

	public void load(int item) {
		StudentPresenter presenter = (StudentPresenter) studentSwitcher
				.getWidget(item);
		presenter.load();
	}

	@Override
	public void handleCrumb() {
		com.sagarius.goddess.client.Utils.setActiveMainMenu("student");
	}
}
