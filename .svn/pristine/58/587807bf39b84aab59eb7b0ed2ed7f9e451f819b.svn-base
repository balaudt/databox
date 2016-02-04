package com.sagarius.goddess.client.utils;

import org.restlet.client.Client;
import org.restlet.client.data.Protocol;

import com.google.gwt.gdata.client.photos.PicasaService;

public class Singletons {
	private static Client CLIENT;

	public static Client getClient() {
		if (CLIENT == null) {
			CLIENT = new Client(Protocol.HTTP);
		}
		return CLIENT;
	}

	private static PicasaService PICASA_SERVICE;

	// XXX GData should be loaded before initializing PicasaService
	public static PicasaService getPicasaService() {
		if (PICASA_SERVICE != null) {
			return PICASA_SERVICE;
		}
		return PICASA_SERVICE = PicasaService.newInstance("Goddess");
	}
}
