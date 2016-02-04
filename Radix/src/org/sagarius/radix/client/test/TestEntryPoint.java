package org.sagarius.radix.client.test;

import org.sagarius.radix.client.view.GWTCProgress;
import org.sagarius.radix.client.view.Table;
import org.sagarius.radix.client.view.Table.SortType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class TestEntryPoint implements EntryPoint {

	int status = 0;

	@Override
	public void onModuleLoad() {
		if (!isDev()) {
			return;
		}
		Table table = new Table();
		table.addHeader("Subject", SortType.ALPHA_NUMERIC);
		table.addHeader("Obtained", SortType.NUMERIC);
		String subjects[] = new String[] { "language", "english", "commerce",
				"economics", "accountancy", "computerscience" };
		int marks[] = new int[] { 143, 158, 111, 119, 113, 184 };
		for (int i = 0; i < subjects.length; i++) {
			table.setText(i, 0, subjects[i]);
			table.setText(i, 1, marks[i] + "");
		}
		RootPanel.get().add(table);
	}

	void iframeWorkAround() {
		IFrameElement iFrame = DOM.createIFrame().cast();
		iFrame.setAttribute("src", "http://www.google.com");
		// RootPanel.get().getElement().appendChild(iFrame);
		IFrameWrapper wrapper = new IFrameWrapper(iFrame);
		RootPanel.get().add(wrapper);
		// .addLoadHandler(new LoadHandler() {
		//
		// @Override
		// public void onLoad(LoadEvent event) {
		// Window.alert("Body load hai!");
		// }
		// });
		// new IFrameWrapper(iFrame).addLoadHandler(new LoadHandler() {
		//
		// @Override
		// public void onLoad(LoadEvent event) {
		// Window.alert("Ok na !");
		// }
		// });
	}

	class IFrameWrapper extends Widget implements HasLoadHandlers {

		public IFrameWrapper(Element a) {
			setElement(a);
		}

		@Override
		public HandlerRegistration addLoadHandler(LoadHandler handler) {
			return addDomHandler(handler, LoadEvent.getType());
		}

		@Override
		protected void onLoad() {
			Window.alert("Something");
		}
	}

	public void progressBarCheck() {
		if (!isDev()) {
			return;
		}
		final GWTCProgress progressBar = new GWTCProgress(5,
				GWTCProgress.SHOW_TIME_REMAINING);
		RootPanel.get().add(progressBar);
		final Timer timer = new Timer() {

			@Override
			public void run() {
				progressBar.setProgress(status++, 5);
				if (status != 5) {
					schedule(1000);
				}
			}
		};
		timer.schedule(1000);
		progressBar.show();
	}

	private boolean isDev() {
		return false;
	}
}
