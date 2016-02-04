package com.sagarius.goddess.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sagarius.goddess.client.ajax.GenericClient;
import com.sagarius.goddess.client.ajax.GenericRequest;
import com.sagarius.goddess.client.ajax.GenericResponse;
import com.sagarius.goddess.client.utils.Print;
import com.sagarius.goddess.client.view.util.ControlPanel;

public abstract class BaseWidget extends Composite {
	@UiField
	protected HTML title;
	@UiField
	protected ControlPanel controlsPanel;
	@UiField
	protected DeckPanel contentDecker;
	@UiField
	protected FlowPanel contentsPanel;
	@UiField
	protected HTMLPanel mainPanel;
	@UiField
	protected FlowPanel header;
	@UiField
	protected FlowPanel footer;
	@UiField
	protected HTML statusMessage;

	protected abstract void initialize(AsyncCallback<Void> callback);

	protected abstract void buildRequest(AsyncCallback<Void> callback);

	protected GenericRequest request;
	protected boolean isLoading;
	protected boolean isLoaded;
	private GenericClient client = new GenericClient();
	protected short retryCount = 0;
	protected short maxRetryCount = 2;
	protected boolean isInitialized;

	public void refresh() {
		setLoading(true);
		if (!isInitialized) {
			initialize(new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					isInitialized = true;
					refresh();
				}

				@Override
				public void onFailure(Throwable caught) {
					statusMessage.setHTML(caught.getMessage());
					setLoading(false);
					return;
				}
			});
			return;
		}
		buildRequest(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				client.handle(request, new AsyncCallback<GenericResponse>() {

					@Override
					public void onSuccess(GenericResponse result) {
						boolean isModified = client.isModified();
						statusMessage.setHTML("");
						if (!isModified) {
							setLoading(false);
							return;
						}
						contentsPanel.clear();
						responseReceived(result);
						retryCount = 0;
						setLoading(false);
					}

					@Override
					public void onFailure(Throwable caught) {
						if (retryCount == maxRetryCount) {
							statusMessage.setHTML(caught.getMessage());
							setLoading(false);
							return;
						}
						retryCount++;
						refresh();
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				statusMessage.setHTML(caught.getMessage());
				setLoading(false);
				return;
			}
		});
	}

	@Override
	public void setTitle(String title) {
		this.title.setText(title);
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	private static BaseWidgetUiBinder uiBinder = GWT
			.create(BaseWidgetUiBinder.class);

	interface BaseWidgetUiBinder extends UiBinder<Widget, BaseWidget> {
	}

	public BaseWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
		if (isLoading) {
			contentDecker.showWidget(0);
		} else {
			contentDecker.showWidget(1);
			isLoaded = true;
		}
		controlsPanel.setEnabled(!isLoading);
	}

	public abstract void responseReceived(GenericResponse genericResponse);

	public void print() {
		Print.it(contentsPanel);
	}
}
