package com.sagarius.goddess.client.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.model.util.EntryCollection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.Document;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.view.util.BoxItemWidget;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class FilesPresenter extends BaseWidget implements CrumbHandler {
	public FilesPresenter() {
		setTitle("Control panel");
		title.setStyleName("h18 strong mb2");
		refresh();
	}

	@Override
	public void handleCrumb() {
		((Label) Breadcrumb.getCrumbAt(1, false)).setText("Files");
		Breadcrumb.ensureMaxLength(2);
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Files");
		}
		com.sagarius.goddess.client.Utils.setActiveMainMenu("files");
		HiddenRowButtons.clear();
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
	public void responseReceived(GenericResponse gResponse) {
		Response response = gResponse.getResponse();
		try {
			String entityText = response.getEntity().getText();
			EntryCollection collection = EntryCollection.create(entityText);
			int size = collection.size();
			LinkedList<Document> result = new LinkedList<Document>();
			for (int i = 0; i < size; i++) {
				result.add(new Document(collection.get(i)));
			}
			Map<DocumentType, String> docMap = new HashMap<DocumentType, String>();
			for (Document document : result) {
				docMap.put(document.getType(), document.getCccKey());
			}
			String cccKey = docMap.get(DocumentType.CLASS);
			contentsPanel.add(new BoxItemWidget(6, "icon-32-building",
					"Classes").addControl(
					new Anchor("View", Utils.sheetViewUrl + "?output=html&key="
							+ cccKey, "_blank")).addControl(
					new Anchor("Change", Utils.sheetViewUrl + "?key=" + cccKey,
							"_blank")));
			cccKey = docMap.get(DocumentType.SINGLE_STUDENT);
			contentsPanel.add(new BoxItemWidget(6, "icon-32-vcard_edit",
					"Students").addControl(
					new Anchor("View", Utils.sheetViewUrl + "?output=html&key="
							+ cccKey, "_blank")).addControl(
					new Anchor("Change", Utils.sheetViewUrl + "?key=" + cccKey,
							"_blank")));
			cccKey = docMap.get(DocumentType.STAFF);
			contentsPanel.add(new BoxItemWidget(6, "icon-32-user_female",
					"Teachers").addControl(
					new Anchor("View", Utils.sheetViewUrl + "?output=html&key="
							+ cccKey, "_blank")).addControl(
					new Anchor("Change", Utils.sheetViewUrl + "?key=" + cccKey,
							"_blank")));
			cccKey = docMap.get(DocumentType.CSSREL);
			contentsPanel.add(new BoxItemWidget(6,
					"icon-32-table_relationship", "Class-Subject-Teacher",
					false, true).addControl(
					new Anchor("View", Utils.sheetViewUrl + "?output=html&key="
							+ cccKey, "_blank")).addControl(
					new Anchor("Change", Utils.sheetViewUrl + "?key=" + cccKey,
							"_blank")));
		} catch (Exception caught) {
			caught.printStackTrace();
		}
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		if (request == null) {
			request = new GenericRequest().setType(RequestType.HTTP);
		}
		request.setHttpRequest(new Request(Method.GET, "/documents"));
		callback.onSuccess(null);
	}
}
