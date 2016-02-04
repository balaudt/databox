package com.sagarius.goddess.client.view;

import java.io.IOException;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.util.BoxItemWidget;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.view.util.LoadingPanel;
import com.sagarius.goddess.client.view.util.MessagePopup;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class ExamsPresenter extends BaseWidget implements CrumbHandler {
	public ExamsPresenter() {
		setTitle("Examinations");
		title.setStyleName("h18 strong mb2");
		refresh();
	}

	@Override
	public void handleCrumb() {
		((Label) Breadcrumb.getCrumbAt(1, false)).setText("Exams");
		Breadcrumb.ensureMaxLength(2);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Exams");
		}
		com.sagarius.goddess.client.Utils.setActiveMainMenu("exam");

		HiddenRowButtons.clear();
		HiddenRowButtons.add(new Hyperlink("[+] Add exam", "addexam"));
		HiddenRowButtons.setRefreshHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}

	@Override
	protected void initialize(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse response) {
		try {
			String responseString = response.getResponse().getEntity()
					.getText();
			List<Document> result = Utils.getDocuments(responseString);
			int i = 0;
			for (Document document : result) {
				BoxItemWidget item = new BoxItemWidget(8,
						"icon-32-page_white_edit", new Hyperlink(
								document.getDocTitle(), "exam/"
										+ document.getCccKey()), i % 3 == 0,
						(i + 1) % 3 == 0);
				contentsPanel.add(item);
				final String ccckey = document.getCccKey();
				item.addControl(new Anchor("View", Utils.sheetViewUrl
						+ "?output=html&key=" + ccckey, "_blank"));
				item.addControl(new Anchor("Edit", Utils.sheetViewUrl + "?key="
						+ ccckey, "_blank"));
				final Anchor refreshButton = new Anchor("Calculate",
						"javascript:");
				refreshButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						refreshButton.setEnabled(false);
						LoadingPanel.INSTANCE.setMessage(
								"Initializing process...").center();
						Singletons.getClient().handle(
								new Request(Method.PUT,
										"/nexam?generate=true&ccc=" + ccckey),
								new Uniform() {

									@Override
									public void handle(Request request,
											Response response) {
										LoadingPanel.INSTANCE.hide();
										if (response.getStatus().isError()) {
											MessagePopup.INSTANCE
													.setMessage(
															"There has been a problem.  Please try again after some time")
													.center();
										} else {
											MessagePopup.INSTANCE.setMessage(
													"Process initiated")
													.center();
										}
										refreshButton.setEnabled(true);
									}
								});
					}
				});
				item.addControl(refreshButton);
				Anchor deleteButton = new Anchor("Delete");
				item.addControl(deleteButton);
				deleteButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						LoadingPanel.INSTANCE.setMessage("Deleting...")
								.center();
						Singletons.getClient().handle(
								new Request(Method.DELETE, "/nexam?ccc="
										+ ccckey), new Uniform() {

									@Override
									public void handle(Request request,
											Response response) {
										LoadingPanel.INSTANCE.hide();
										if (response.getStatus().isError()) {
											MessagePopup.INSTANCE
													.setMessage(
															"There has been a problem.  Please try again after some time")
													.center();
										} else {
											refresh();
										}
									}
								});
					}
				});
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			statusMessage.setText("Error parsing response from the server");
		}
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		if (request == null) {
			request = new GenericRequest().setType(RequestType.HTTP);
		}
		request.setHttpRequest(new Request(Method.GET, "/documents?type="
				+ DocumentType.EXAMINATION));
		callback.onSuccess(null);
	}
}
