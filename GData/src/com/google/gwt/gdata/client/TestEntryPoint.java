package com.google.gwt.gdata.client;

import com.google.gwt.accounts.client.AuthSubSettings;
import com.google.gwt.accounts.client.AuthSubStatus;
import com.google.gwt.accounts.client.User;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.gdata.client.impl.CallErrorException;
import com.google.gwt.gdata.client.impl.Callback;
import com.google.gwt.gdata.client.photos.PhotoEntry;
import com.google.gwt.gdata.client.photos.PhotoFeed;
import com.google.gwt.gdata.client.photos.PhotoFeedCallback;
import com.google.gwt.gdata.client.photos.PhotoQuery;
import com.google.gwt.gdata.client.photos.PicasaService;
import com.google.gwt.gdata.client.spreadsheet.CellEntry;
import com.google.gwt.gdata.client.spreadsheet.CellFeed;
import com.google.gwt.gdata.client.spreadsheet.CellFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.CellQuery;
import com.google.gwt.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gwt.gdata.client.spreadsheet.ListFeed;
import com.google.gwt.gdata.client.spreadsheet.ListFeedCallback;
import com.google.gwt.gdata.client.spreadsheet.ListQuery;
import com.google.gwt.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class TestEntryPoint implements EntryPoint {

	private final class NewRunnable implements Runnable {

		@Override
		public void run() {
			FeedURLFactory factory = FeedURLFactory.getDefault();
			String scope = factory.getBaseUrl() + "feeds/";
			AuthSubStatus status = User.getStatus(scope);
			if (status == AuthSubStatus.LOGGED_OUT) {
				AuthSubSettings settings = AuthSubSettings.newInstance();
				// settings.setHd("databox.co.in");
				User.login(scope, settings);
				return;
			}
			SpreadsheetService service = SpreadsheetService
					.newInstance("Goddess");
			String listFeedUrl = factory.getListFeedUrl(
					"0AuPgApigQS6SdGpJYUpsWW1SYm1Ockp3aVZOUUZJU0E", "od6",
					"private", "basic");
			ListQuery query = ListQuery.newInstance(listFeedUrl);
			// query.setMaxResults(10);
			query.setUpdatedMin("2010-08-07T10:12:00.932Z");
			service.getListFeed(query, new ListFeedCallback() {

				@Override
				public void onSuccess(ListFeed result) {
					System.out.println("Fine");
				}

				@Override
				public void onFailure(CallErrorException caught) {
					caught.printStackTrace();
				}
			});
		}
	}

	@Override
	public void onModuleLoad() {
		if (!isDev()) {
			return;
		}
		GData.loadGDataApi(null, "2.0", new NewRunnable());
	}

	protected void loadNew() {
		RootPanel panel = RootPanel.get();
		final Label statusLabel = new Label();
		panel.add(statusLabel);
		panel.add(new Image(""));
		String scope = "http://picasaweb.google.com/data/";
		AuthSubStatus status = User.getStatus(scope);
		if (status == AuthSubStatus.LOGGED_OUT) {
			AuthSubSettings settings = AuthSubSettings.newInstance();
			// settings.setHd("databox.co.in");
			User.login(scope, settings);
			return;
		}
		PicasaService service = PicasaService.newInstance("Goddess");
		final PhotoQuery query = PhotoQuery
				.newInstance("http://picasaweb.google.com/data/feed/api/user/default");
		query.setKind("photo");
		query.setStringParam("fields", "entry(content)");
		query.setTag("1001");
		service.getPhotoFeed(query, new PhotoFeedCallback() {

			@Override
			public void onSuccess(PhotoFeed result) {
				PhotoEntry entry = result.getEntries()[0];
				System.out.println(entry.getImageUrl());
			}

			@Override
			public void onFailure(CallErrorException caught) {
				caught.printStackTrace();
			}
		});
	}

	private boolean isDev() {
		return false;
	}

	protected void load() {
		RootPanel panel = RootPanel.get();
		final Label statusLabel = new Label();
		panel.add(statusLabel);
		panel.add(new Image(""));
		FeedURLFactory factory = FeedURLFactory.getDefault();
		String scope = factory.getBaseUrl() + "feeds/";
		System.out.println(scope);
		AuthSubStatus status = User.getStatus(scope);
		if (status == AuthSubStatus.LOGGED_OUT) {
			AuthSubSettings settings = AuthSubSettings.newInstance();
			// settings.setHd("databox.co.in");
			User.login(scope, settings);
			return;
		}
		SpreadsheetService service = SpreadsheetService.newInstance("Goddess");
		CellQuery query = CellQuery.newInstance(factory.getCellFeedUrl(
				"t7C04MJbQBbEbzz87c8sUkw", "od6", "private", "full"));
		query.setRange("B2");
		service.getCellFeed(query, new CellFeedCallback() {

			@Override
			public void onSuccess(CellFeed result) {
				CellEntry entry = result.getEntries()[0];
				entry.setValue("Tester");
				entry.updateEntry(new Callback<CellEntry>() {

					@Override
					public void onSuccess(CellEntry result) {
						System.out.println("Success");
					}

					@Override
					public void onFailure(CallErrorException caught) {
						caught.printStackTrace();
					}
				});
			}

			@Override
			public void onFailure(CallErrorException caught) {
				caught.printStackTrace();
			}
		});

	}
}
