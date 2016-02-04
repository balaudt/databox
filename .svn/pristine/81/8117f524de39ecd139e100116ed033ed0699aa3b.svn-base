package com.sagarius.goddess.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.sagarius.radix.client.uri.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.HomePresenter;
import com.sagarius.goddess.client.view.parent.StudentsPresenter;

public class ParentIndexPage extends Composite implements
		ValueChangeHandler<String> {
	private Map<String, Integer> widgetMap = new HashMap<String, Integer>();
	private DeckPanel mainPanel = new DeckPanel();
	private String phoneNo;
	@UiField
	FlowPanel banner;
	@UiField
	UListElement menubar;
	@UiField
	FlowPanel breadcrumb;
	@UiField
	FlowPanel credentials;
	@UiField
	FlowPanel searcher;
	@UiField
	FlowPanel container;
	@UiField
	HTMLPanel topNav;
	@UiField
	FlowPanel utilitiesContainer;
	@UiField
	FlowPanel blueStrip;
	List<Template> templates = new ArrayList<Template>();
	private static ParentIndexPageUIBinder uiBinder = GWT
			.create(ParentIndexPageUIBinder.class);

	public ParentIndexPage() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void onLoad(String templateUri, boolean isNew) {
		if (isNew) {
			int index = mainPanel.getWidgetCount() - 1;
			widgetMap.put(templateUri, index);
		}
		Integer widgetIndex = widgetMap.get(templateUri);
		mainPanel.showWidget(widgetIndex);
		Widget widget = mainPanel.getWidget(widgetIndex);
		((CrumbHandler) widget).handleCrumb();
		// if (widget instanceof StatefulWidget) {
		// ((StatefulWidget) widget).handle(Utils.getAttributes(templateUri,
		// ((StatefulWidget) widget).getTemplate()));
		// }
	}

	@UiTemplate("IndexPage.ui.xml")
	interface ParentIndexPageUIBinder extends UiBinder<Widget, ParentIndexPage> {
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		handleToken(event.getValue());
	}

	public void handleToken(final String token) {
		String templateString = null;
		for (Template template : templates) {
			if (template.matches(token)) {
				templateString = template.getUri();
				break;
			}
		}
		if (templateString == null) {
			// TODO No such page exists
			return;
		}
		final String templateUri = templateString;
		Integer widgetIndex = widgetMap.get(templateString);
		if (widgetIndex == null) {
			if (token.equals("home")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						// Here add a route if the widget needs to refresh based
						// on
						// parameters
						mainPanel.add(new HomePresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("student")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new StudentsPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});

			}
		} else {
			onLoad(templateUri, false);
		}
	}

	public void initComponents() {
		RootPanel.get("outer-container").getElement()
				.setAttribute("style", "display: block");
		RootPanel.get("footer").getElement()
				.setAttribute("style", "display: block");
		blueStrip.remove(searcher);
		blueStrip.remove(breadcrumb);
		initializeMenus();
		banner.add(new Image("/photo?size=0&id=banner"));
		Anchor logoutLink = new Anchor("Logout", "javascript:");
		logoutLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Singletons.getClient().handle(
						new Request(Method.DELETE, "/login"), new Uniform() {

							@Override
							public void handle(Request request,
									Response response) {
								String nextUrl = URL.encode(Window.Location
										.getHref());
								Window.Location.replace("/login.html?nextUrl="
										+ nextUrl);
							}
						});
				Collection<String> cookies = Cookies.getCookieNames();
				for (String cookie : cookies) {
					Cookies.removeCookie(cookie);
				}
			}
		});
		HTML userHtml = new HTML("Welcome " + phoneNo);
		userHtml.setStyleName("fl mr4");
		credentials.add(userHtml);
		credentials.add(logoutLink);

		templates = Arrays.asList(new Template[] { new Template("home"),
				new Template("student"), new Template("student/{studentId}") });
		String token = History.getToken();
		if (token == null || token.trim().equals("")) {
			token = "home";
			History.newItem("home", false);
		}
		handleToken(token);

		// Main widgets display
		container.add(mainPanel);
	}

	private void initializeMenus() {
		// Menu related UI changes
		topNav.getElement().setId("top-navigation-container");
		menubar.setId("menubar");
		utilitiesContainer.getElement().setId("utilities-container");
		int[] menus = AuthorizationLevels.getMenus(MemberType.PARENT);
		for (int menuIndex : menus) {
			String[] link = AuthorizationLevels.MENU_ITEMS.get(menuIndex);
			LIElement liElement = LIElement.as(DOM.createElement("li"));
			AnchorElement anchor = AnchorElement.as(DOM.createAnchor());
			anchor.setInnerHTML(link[0]);
			anchor.setHref("#" + link[1]);
			liElement.appendChild(anchor);
			menubar.appendChild(liElement);
		}
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return phoneNo;
	}

}
