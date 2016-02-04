package com.sagarius.goddess.client.view.settings;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.data.Method;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.ajax.RequestType;
import com.sagarius.goddess.client.model.School;
import com.sagarius.goddess.client.model.enumerations.SchoolType;
import com.sagarius.goddess.client.view.settings.FormPanel.ElementValue;
import com.sagarius.goddess.client.view.settings.FormPanel.FormElement;
import com.sagarius.goddess.client.widgets.BaseWidget;

public class GeneralPage extends BaseWidget {
	private BasePage mainPage = new BasePage() {

		@Override
		public String getHelp() {
			return "<p>General settings for the school could be set here. These settings could be changed per requirement later.</p><strong>Category:</strong><p>It must not be changed without proper assistance. It could modify some of the data pertaining to your institution. For assistance, kindly contact technical support.</p><strong>Mobile Number:</strong><p>Alerts regarding the application status will be sent to the mobile(s) number listed here.</p><strong>Staff Count:</strong><p>May or may not include non-teaching staff.</p>";
		}
	};
	private Label nameLabel;
	private Label addressLabel;
	private Label phoneLabel;
	private Label mobileLabel;
	private Label siteLabel;
	private Label mailLabel;
	private Label categoryLabel;
	private Label studLabel;
	private Label staffLabel;
	private TextBox nameBox;
	private TextBox addressBox;
	private TextBox phoneBox;
	private TextBox mobileBox;
	private TextBox siteBox;
	private TextBox mailBox;
	private ListBox categoryBox;
	private TextBox studBox;
	private TextBox staffBox;

	public void setValue(School school) {
		nameLabel.setText(school.getName());
		addressLabel.setText(school.getAddress());
		phoneLabel.setText(school.getPhoneNo());
		mobileLabel.setText(school.getMobile());
		siteLabel.setText(school.getSiteUrl());
		mailLabel.setText(school.getEmail());
		categoryLabel.setText(SchoolType.valueOf(school.getType())
				.getTypeString());
		studLabel.setText(school.getStudentCount() + "");
		staffLabel.setText(school.getStaffCount() + "");

		nameBox.setText(school.getName());
		addressBox.setText(school.getAddress());
		phoneBox.setText(school.getPhoneNo());
		mobileBox.setText(school.getMobile());
		siteBox.setText(school.getSiteUrl());
		mailBox.setText(school.getEmail());
		categoryBox.setSelectedIndex(SchoolType.valueOf(school.getType())
				.ordinal());
		studBox.setText(school.getStudentCount() + "");
		staffBox.setText(school.getStaffCount() + "");
	}

	public GeneralPage() {
		nameLabel = new Label();
		addressLabel = new Label();
		phoneLabel = new Label();
		mobileLabel = new Label();
		siteLabel = new Label();
		mailLabel = new Label();
		categoryLabel = new Label();
		studLabel = new Label();
		staffLabel = new Label();

		Button editButton = new Button("Edit");
		editButton.setStyleName("gbutton");
		mainPage.viewPanel
				.addElement(
						new FormElement().setName("Name:").setValue(
								new ElementValue().setWidget(nameLabel)))
				.addElement(
						new FormElement().setName("Address:").setValue(
								new ElementValue().setWidget(addressLabel)))
				.addElement(
						new FormElement().setName("Phone Number:").setValue(
								new ElementValue().setWidget(phoneLabel)))
				.addElement(
						new FormElement().setName("Mobile Number:").setValue(
								new ElementValue().setWidget(mobileLabel)))
				.addElement(
						new FormElement().setName("Website:").setValue(
								new ElementValue().setWidget(siteLabel)))
				.addElement(
						new FormElement().setName("E-mail:").setValue(
								new ElementValue().setWidget(mailLabel)))
				.addElement(
						new FormElement().setName("Category:").setValue(
								new ElementValue().setWidget(categoryLabel)))
				.addElement(
						new FormElement().setName("Student Count:").setValue(
								new ElementValue().setWidget(studLabel)))
				.addElement(
						new FormElement().setName("Staff Count:").setValue(
								new ElementValue().setWidget(staffLabel)))
				.addElement(
						new FormElement().setName("&nbsp;").setValue(
								new ElementValue().setWidget(editButton)));
		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mainPage.setState(true);
			}
		});

		nameBox = new TextBox();
		nameBox.setName("name");
		addressBox = new TextBox();
		addressBox.setName("address");
		phoneBox = new TextBox();
		phoneBox.setName("phone");
		mobileBox = new TextBox();
		mobileBox.setName("mobile");
		siteBox = new TextBox();
		siteBox.setName("site");
		mailBox = new TextBox();
		mailBox.setName("mail");
		categoryBox = new ListBox(false);
		categoryBox.setName("category");
		SchoolType[] values = SchoolType.values();
		for (SchoolType type : values) {
			categoryBox.addItem(type.getTypeString(), type.toString());
		}
		studBox = new TextBox();
		studBox.setName("student");
		staffBox = new TextBox();
		staffBox.setName("staff");
		Button saveButton = new Button("Save");
		Button cancelButton = new Button("Cancel");
		saveButton.setStyleName("gbutton");
		cancelButton.setStyleName("gbutton");
		mainPage.editPanel
				.addElement(
						new FormElement()
								.setName("Name:")
								.setValue(
										new ElementValue()
												.setWidget(nameBox)
												.setMeta(
														" Ex: CMS Matriculation Higher Secondary School ")))
				.addElement(
						new FormElement()
								.setName("Address:")
								.setValue(
										new ElementValue()
												.setWidget(addressBox)
												.setMeta(
														"Ex: No: 831, Sathy Road, Ganapathy, Coimbatore - 641 006")))
				.addElement(
						new FormElement()
								.setName("Phone Number:")
								.setValue(
										new ElementValue()
												.setWidget(phoneBox)
												.setMeta(
														"Seperate multiple entries using commas.")
												.setMeta(
														"Ex: 0422-2531564, 0422-5687676")))
				.addElement(
						new FormElement()
								.setName("Mobile Number:")
								.setValue(
										new ElementValue()
												.setWidget(mobileBox)
												.setMeta(
														"Seperate multiple entries using commas, do not add country code before.")
												.setMeta(
														"Ex: 9600984220, 9597019456")))
				.addElement(
						new FormElement()
								.setName("Website:")
								.setValue(
										new ElementValue()
												.setWidget(siteBox)
												.setMeta(
														"Ex: www.cmsmhss.com (Do not enter multiple entries)")))
				.addElement(
						new FormElement()
								.setName("E-mail:")
								.setValue(
										new ElementValue()
												.setWidget(mailBox)
												.setMeta(
														"Ex: cmshrss@yahoo.co.in (Do not enter multiple entries)")))
				.addElement(
						new FormElement()
								.setName("Category:")
								.setValue(
										new ElementValue()
												.setWidget(categoryBox)
												.setMeta(
														"Select the type of the institution.")))
				.addElement(
						new FormElement().setName("Student Count:").setValue(
								new ElementValue().setWidget(studBox).setMeta(
										"Enter integer value. Ex: 3000")))
				.addElement(
						new FormElement().setName("Staff Count:").setValue(
								new ElementValue().setWidget(staffBox).setMeta(
										"Enter integer value. Ex: 120")))
				.addElement(
						new FormElement().setName("&nbsp;").setValue(
								new ElementValue().setWidget(saveButton)
										.setWidget(cancelButton)));
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mainPage.setState(false);
			}
		});

		mainPage.editFormPanel.setAction("/school?m=PUT");
		mainPage.editFormPanel.setMethod("post");
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				mainPage.editFormPanel.submit();
			}
		});
		mainPage.editFormPanel.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				setLoading(true);
			}
		});
		mainPage.editFormPanel
				.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						setLoading(false);
						setValue(new School(event.getResults()));
						mainPage.setState(false);
					}
				});
		refresh();
	}

	@Override
	protected void initialize(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	@Override
	protected void buildRequest(AsyncCallback<Void> callback) {
		request = new GenericRequest().setType(RequestType.HTTP)
				.setHttpRequest(new Request(Method.GET, "/school"));
		callback.onSuccess(null);
	}

	@Override
	public void responseReceived(GenericResponse genericResponse) {
		try {
			setValue(new School(genericResponse.getResponse().getEntity()
					.getText()));
			contentsPanel.add(mainPage);
		} catch (IOException e) {
			statusMessage.setText("There was a problem loading school data");
		}
	}

}
