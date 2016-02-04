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
import org.sagarius.radix.client.uri.StatefulWidget;
import org.sagarius.radix.client.uri.Template;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.present.CrumbHandler;
import com.sagarius.goddess.client.utils.SearchSuggestOracle;
import com.sagarius.goddess.client.utils.SearchSuggestion;
import com.sagarius.goddess.client.utils.Singletons;
import com.sagarius.goddess.client.view.AddExamPresenter;
import com.sagarius.goddess.client.view.AttendancePresenter;
import com.sagarius.goddess.client.view.ClassPresenter;
import com.sagarius.goddess.client.view.ClassesPresenter;
import com.sagarius.goddess.client.view.DeprecatedExamPresenter;
import com.sagarius.goddess.client.view.ExamsPresenter;
import com.sagarius.goddess.client.view.FilesPresenter;
import com.sagarius.goddess.client.view.HomePresenter;
import com.sagarius.goddess.client.view.ExamPresenter;
import com.sagarius.goddess.client.view.SMSPresenter;
import com.sagarius.goddess.client.view.SettingsPresenter;
import com.sagarius.goddess.client.view.StaffPresenter;
import com.sagarius.goddess.client.view.StaffsPresenter;
import com.sagarius.goddess.client.view.StudentPresenter;
import com.sagarius.goddess.client.view.util.Breadcrumb;
import com.sagarius.goddess.client.view.util.HiddenRowButtons;

public class IndexPage extends Composite implements ValueChangeHandler<String> {
	private Map<String, Integer> widgetMap = new HashMap<String, Integer>();
	private DeckPanel mainPanel = new DeckPanel();
	private MemberType type = MemberType.ADMINISTRATOR;
	private String email;
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
	FlowPanel searchHide;

	private static IndexPageUiBinder uiBinder = GWT
			.create(IndexPageUiBinder.class);

	interface IndexPageUiBinder extends UiBinder<Widget, IndexPage> {
	}

	public IndexPage() {
		initWidget(uiBinder.createAndBindUi(this));
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
			} else if (token.equals("class")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new ClassesPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("class")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new ClassPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("staff")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new StaffsPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("staff")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new StaffPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("exam")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new ExamsPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("depexam")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new DeprecatedExamPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("student")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new StudentPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("files")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new FilesPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("addexam")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new AddExamPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("sms")) {

				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new SMSPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("exam")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new ExamPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.startsWith("settings")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new SettingsPresenter());
						onLoad(token, true);
					}

					@Override
					public void onFailure(Throwable reason) {
						reason.printStackTrace();
					}
				});
			} else if (token.equals("attendance")) {
				GWT.runAsync(new RunAsyncCallback() {

					@Override
					public void onSuccess() {
						mainPanel.add(new AttendancePresenter());
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

	public void onLoad(String templateUri, boolean isNew) {
		if (isNew) {
			int index = mainPanel.getWidgetCount() - 1;
			widgetMap.put(templateUri, index);
		}
		Integer widgetIndex = widgetMap.get(templateUri);
		mainPanel.showWidget(widgetIndex);
		Widget widget = mainPanel.getWidget(widgetIndex);
		((CrumbHandler) widget).handleCrumb();
		if (widget instanceof StatefulWidget) {
			((StatefulWidget) widget).handle(Utils.getAttributes(templateUri,
					((StatefulWidget) widget).getTemplate()));
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		handleToken(event.getValue());
	}

	public void initComponents() {
		RootPanel.get("outer-container").getElement()
				.setAttribute("style", "display: block");
		RootPanel.get("footer").getElement()
				.setAttribute("style", "display: block");

		initializeMenus();

		banner.add(new Image("/photo?size=0&id=banner"));
		breadcrumb.add(Breadcrumb.init());

		initializeSearch();

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
		HTML userHtml = new HTML("Welcome " + email);
		userHtml.setStyleName("fl mr4");
		credentials.add(userHtml);
		credentials.add(logoutLink);

		// History management
		templates = Arrays.asList(new Template[] { new Template("home"),
				new Template("staff"), new Template("staff/{staffId}"),
				new Template("student"), new Template("student/{studentId}"),
				new Template("class"), new Template("class/{classId}"),
				new Template("exam"), new Template("exam/{examId}"),
				new Template("files"), new Template("addexam"),
				new Template("sms"), new Template("depexam/{examId}"),
				new Template("settings"), new Template("attendance") });
		String token = History.getToken();
		if (token == null || token.trim().equals("")) {
			token = "home";
			History.newItem("home", false);
		}
		handleToken(token);

		// Main widgets display
		container.add(mainPanel);
	}

	private void initializeSearch() {
		// Search related UI
		searcher.getElement().setId("search");
		HTML searchHtml = new HTML("search");
		searchHtml
				.getElement()
				.setAttribute(
						"style",
						"size: 12px; color: rgb(166, 173, 180); text-transform: uppercase; font-weight: bold;");
		searcher.add(searchHtml);
		final SearchSuggestOracle oracle = new SearchSuggestOracle();
		oracle.setType(1);
		SuggestBox searchBox = new SuggestBox(oracle);
		searchBox.setStyleName("");
		searchBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				Suggestion suggestion = event.getSelectedItem();
				if (!(suggestion instanceof SearchSuggestion)) {
					return;
				}
				SearchSuggestion searchSuggestion = (SearchSuggestion) suggestion;
				switch (searchSuggestion.getType()) {
				case STAFF:
					History.newItem("staff/" + searchSuggestion.getId(), true);
					return;
				case STUDENT:
					History.newItem("student/" + searchSuggestion.getId(), true);
				}
			}
		});
		searcher.add(searchBox);
		Anchor searchButton = new Anchor(
				"<img src='goddess/images/1279021033_001_38.gif' />", true,
				"javascript:");
		searchButton.getElement().setId("search-button");
		searcher.add(searchButton);

		ValueChangeHandler<Boolean> typeHandler = new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				RadioButton radio = (RadioButton) event.getSource();
				oracle.setType(Integer.parseInt(radio.getFormValue()));
			}
		};
		FlowPanel searchTypeContainer = new FlowPanel();
		searchTypeContainer.setStyleName("");
		searchTypeContainer.getElement().setId("search-select");
		// RadioButton searchType = new RadioButton("searchType", "STaff");
		// searchType.setStyleName("fl");
		// searchType.setFormValue("0");
		// searchType.addValueChangeHandler(typeHandler);
		// searchTypeContainer.add(searchType);
		RadioButton searchType = new RadioButton("searchType", "Students");
		searchType.setStyleName("fl");
		searchType.setFormValue("1");
		searchType.setValue(true);
		searchType.addValueChangeHandler(typeHandler);
		searchTypeContainer.add(searchType);
		searchType = new RadioButton("searchType", "Staffs");
		searchType.setStyleName("fl");
		searchType.setFormValue("2");
		searchType.addValueChangeHandler(typeHandler);
		searchTypeContainer.add(searchType);
		searcher.add(searchTypeContainer);

		HiddenRowButtons.init(searcher, searchHide);
	}

	private void initializeMenus() {
		// Menu related UI changes
		topNav.getElement().setId("top-navigation-container");
		menubar.setId("menubar");
		utilitiesContainer.getElement().setId("utilities-container");
		int[] menus = AuthorizationLevels.getMenus(getType());
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

	public void setType(MemberType type) {
		this.type = type;
	}

	public MemberType getType() {
		return type;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	List<Template> templates = new ArrayList<Template>();
}
