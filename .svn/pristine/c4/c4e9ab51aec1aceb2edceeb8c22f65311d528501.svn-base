package com.sagarius.goddess.client.view;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.Method;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.sagarius.goddess.client.Utils;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.gadgets.ProfileGadget;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget;
import com.sagarius.goddess.client.gadgets.ProfileGadget.ProfileDetails.FormWidget.FormElement;
import com.sagarius.goddess.client.model.School;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.view.util.Breadcrumb;

public class HomePresenter extends Composite implements CrumbHandler {
	public HomePresenter() {
		ProfileView view = new ProfileView();
		initWidget(view);
		view.refresh();
	}

	@Override
	public void handleCrumb() {
		Breadcrumb.ensureMaxLength(1);
		com.sagarius.goddess.client.Utils.setActiveMainMenu("home");
		if (Utils.currentSchool != null) {
			Window.setTitle("Databox - " + Utils.currentSchool.getName()
					+ " - Home");
		}
	}

}

class ProfileView extends ProfileGadget {

	@Override
	protected void initialize(AsyncCallback<Void> callback) {
		request = new GenericRequest().setType(RequestType.HTTP)
				.setHttpRequest(new Request(Method.GET, "/school"));
		callback.onSuccess(null);
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse gResponse) {
		statusMessage.setText("Parsing response");
		Response response = gResponse.getResponse();
		try {
			String entityText = response.getEntity().getText();
			School school = new School(entityText);
			FormWidget formWidget = new FormWidget();
			setName(school.getName()).setImage("/photo?id=school&size=288",
					true, 7, 258).setDetails(
					new ProfileDetails(17, true).setFormWidget(formWidget));
			formWidget.addElement(new FormElement(false, 4).setMeta("Name")
					.setValue(school.getName()));
			formWidget.addElement(new FormElement(false, 4).setMeta("Address")
					.setValue(school.getAddress()));
			formWidget.addElement(new FormElement(false, 4).setMeta(
					"Phone number").setValue(school.getPhoneNo()));
			formWidget.addElement(new FormElement(false, 4).setMeta("E-mail")
					.setValue(school.getEmail()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		statusMessage.setText("");
	}

}