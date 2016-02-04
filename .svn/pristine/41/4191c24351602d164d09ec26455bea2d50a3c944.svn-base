package com.sagarius.goddess.client.login;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.data.Status;
import org.restlet.client.representation.StringRepresentation;
import org.sagarius.radix.client.model.util.BaseEntry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.model.School;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.util.MessagePopup;

public class LoginPage extends Composite {

	private static LoginPageUiBinder uiBinder = GWT
			.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {
	}

	@UiField
	DivElement schoolName;
	@UiField
	DivElement schoolAddress;
	@UiField
	ParagraphElement contactPara;
	@UiField
	TextBox username;
	@UiField
	PasswordTextBox password;
	@UiField
	Button loginButton;
	@UiField
	Button requestButton;
	@UiField
	TextBox requestText;
	@UiField
	FlowPanel adminLogin;
	@UiField
	Anchor forgotButton;

	public LoginPage(final String nextUrl) {
		initWidget(uiBinder.createAndBindUi(this));
		requestText
				.getElement()
				.setAttribute(
						"style",
						"border-color: rgb(255, 255, 255);width: 240px;margin-top: 10px;margin-left: 18px;font-family: georgia;font-weight: bold;color: #555555;");
		password.getElement()
				.setAttribute(
						"style",
						"border-color: rgb(255, 255, 255);width: 240px;margin-left: 18px;margin-top: 8px;font-family: georgia;font-weight: bold;color: #555555;");
		username.getElement()
				.setAttribute(
						"style",
						"border-color: rgb(255, 255, 255);height: 18px;width: 240px;margin-left: 18px;margin-top: 10px;font-family: georgia;font-weight: bold;color: #555555;");
		forgotButton.getElement().setAttribute("style",
				"color: rgb(51, 153, 255); padding-left: 180px;");

		Singletons.getClient().handle(new Request(Method.GET, "/school"),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						try {
							String responseText = response.getEntity()
									.getText();
							School school = new School(responseText);
							schoolName.setInnerHTML(school.getName());
							schoolAddress.setInnerHTML(school.getAddress());
							String email = school.getEmail();
							contactPara
									.setInnerHTML("For feedback or enquiry, either call us @ "
											+ school.getPhoneNo()
											+ " or send an email to <a style='color: rgb(51, 153, 255);' href='mailto:"
											+ email + "'>" + email + "</a>");
							HTML adminHtml = new HTML();
							StringBuffer buffer = new StringBuffer();
							buffer.append(
									"Adminstrator login: <a href='/login?op=true&nextUrl=")
									.append(nextUrl)
									.append("'>Google account </a> or <a href='/login?op=true&nextUrl=")
									.append(nextUrl).append("&domain=")
									.append(school.getDomainName())
									.append("'> School account </a>");
							adminLogin.add(adminHtml);
							adminHtml.setHTML(buffer.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
		requestButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestButton.setEnabled(false);
				requestText.setEnabled(false);
				Singletons.getClient().handle(
						new Request(Method.POST, "/parent?phone="
								+ requestText.getText()), new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								Status status = response.getStatus();
								if (status == Status.CLIENT_ERROR_CONFLICT) {
									MessagePopup.INSTANCE
											.setMessage(
													"The user has already been registered.  If you have forgot your password, please click on Forgot password")
											.center();
								} else if (!status.isError()) {
									MessagePopup.INSTANCE
											.setMessage(
													"New password has been sent to your mobile")
											.center();
								} else {
									MessagePopup.INSTANCE
											.setMessage(
													"There was a problem processing request")
											.center();
								}
								requestText.setEnabled(true);
								requestButton.setEnabled(true);
							}
						});
			}
		});
		forgotButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				forgotButton.setEnabled(false);
				username.setEnabled(false);
				Singletons.getClient().handle(
						new Request(Method.PUT, "/parent?phone="
								+ username.getText()), new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								Status status = response.getStatus();
								if (status == Status.CLIENT_ERROR_BAD_REQUEST) {
									MessagePopup.INSTANCE.setMessage(
											"No such user").center();
								} else if (!status.isError()) {
									MessagePopup.INSTANCE
											.setMessage(
													"The reset password has been sent to your mobile")
											.center();
								} else {
									MessagePopup.INSTANCE
											.setMessage(
													"There was a problem processing request")
											.center();
								}
								username.setEnabled(true);
								forgotButton.setEnabled(true);
							}
						});
			}
		});
		loginButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loginButton.setEnabled(false);
				username.setEnabled(false);
				password.setEnabled(false);
				BaseEntry entry = BaseEntry.create();
				entry.set(0, username.getText());
				entry.set(1, com.sagarius.goddess.shared.Utils.encryptAsString(password.getText()));
				Singletons.getClient().handle(
						new Request(Method.POST, "/login",
								new StringRepresentation(entry.render())),
						new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								username.setEnabled(true);
								password.setEnabled(true);
								loginButton.setEnabled(true);
								if (response.getStatus().isSuccess()) {
									Window.Location
											.replace(getNextUrl(nextUrl));
								} else {
									MessagePopup.INSTANCE
											.setMessage(
													"There was a problem processing request.")
											.center();
								}
							}
						});
			}
		});
	}

	private String getNextUrl(String nextParam) {
		if (nextParam != null) {
			return nextParam;
		}
		String href = Window.Location.getHref();
		int deleteIndex = href.indexOf("/login.html");
		href = href.substring(0, deleteIndex);
		href += "/index.html";
		if (!GWT.isScript()) {
			href += "?gwt.codesvr=127.0.0.1:9997";
		}
		return href;
	}
}
