package com.google.gwt.gdata.client.photos;

import com.google.gwt.gdata.client.GDataRequestParameters;
import com.google.gwt.gdata.client.GoogleService;

public class PicasaService extends GoogleService {
	protected PicasaService() {
	}

	public static native PicasaService newInstance(String applicationName)/*-{
		return new $wnd.google.gdata.client.GoogleService(applicationName);
	}-*/;

	public final void getPhotoFeed(String uri, PhotoFeedCallback callback) {
		this.getFeed(uri, callback, PhotoFeed.getConstructor(), true, null);
	}

	public final void getPhotoFeed(String uri, PhotoFeedCallback callback,
			GDataRequestParameters parameters) {
		this.getFeed(uri, callback, PhotoFeed.getConstructor(), true,
				parameters);
	}

	public final void getPhotoFeed(PhotoQuery query, PhotoFeedCallback callback) {
		this.getFeed(query, callback, PhotoFeed.getConstructor(), true, null);
	}

	public final void getPhotoFeed(PhotoQuery query,
			PhotoFeedCallback callback, GDataRequestParameters parameters) {
		this.getFeed(query, callback, PhotoFeed.getConstructor(), true,
				parameters);
	}
}
