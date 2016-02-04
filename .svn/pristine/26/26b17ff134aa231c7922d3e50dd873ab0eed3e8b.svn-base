package com.sagarius.goddess.client.gadgets;

import java.io.IOException;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.Method;
import org.restlet.client.data.Status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.utils.Singletons;

public abstract class BaseGadget extends Composite {

	private static BaseGadgetUiBinder uiBinder = GWT
			.create(BaseGadgetUiBinder.class);

	interface BaseGadgetUiBinder extends UiBinder<Widget, BaseGadget> {
	}

	@UiField
	protected HTML title;
	@UiField
	Anchor refreshButton;
	@UiField
	public FlowPanel header;
	@UiField
	public FlowPanel footer;
	@UiField
	DeckPanel contentDecker;
	@UiField
	protected FlowPanel contentsPanel;
	@UiField
	protected FlowPanel controlsPanel;
	@UiField
	FlowPanel statusPanel;
	@UiField
	protected HTML statusMessage;
	protected String requestUri;
	private boolean isLoading;
	protected boolean isLoaded;

	public boolean isLoaded() {
		return isLoaded;
	}

	public BaseGadget(String clazz, String requestUri) {
		initWidget(uiBinder.createAndBindUi(this));
		if (clazz != null) {
			setStyleName(clazz);
		}
		this.requestUri = requestUri;
	}

	/**
	 * @param event
	 */
	@UiHandler("refreshButton")
	void handleClick(ClickEvent event) {
		refresh();
	}

	public void refresh() {
		setLoading(true);
		Singletons.getClient().handle(new Request(Method.GET, requestUri),
				new Uniform() {

					@Override
					public void handle(Request request, Response response) {
						if (isLoaded
								&& response.getStatus().equals(
										Status.REDIRECTION_NOT_MODIFIED)) {
							setLoading(false);
							return;
						}
						if (response.getStatus().isError()) {
							try {
								statusMessage.setHTML(response.getEntity()
										.getText());
							} catch (IOException e) {
								e.printStackTrace();
							}
							setLoading(false);
							return;
						}
						statusMessage.setHTML("");
						contentsPanel.clear();
						responseReceived(response);
						setLoading(false);
					}
				});
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
		if (isLoading) {
			contentDecker.showWidget(0);
		} else {
			contentDecker.showWidget(1);
			isLoaded = true;
		}
		setControlsEnabled(isLoading);
		refreshButton.setEnabled(!isLoading);
	}

	public void setStatusMessage(String html) {
		statusMessage.setHTML(html);
	}

	public boolean isLoading() {
		return isLoading;
	}

	public abstract void responseReceived(Response response);

	public void setTitle(String gadgetTitle) {
		title.setHTML(gadgetTitle);
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	private void setControlsEnabled(boolean isEnabled) {
		int count = controlsPanel.getWidgetCount();
		for (int i = 0; i < count; i++) {
			Widget widget = controlsPanel.getWidget(i);
			if (widget instanceof FocusWidget) {
				((FocusWidget) widget).setEnabled(isEnabled);
			}
		}
		refreshButton.setEnabled(isEnabled);
	}
}
