package com.sagarius.goddess.server.resources.parent;

import java.util.Map;

import org.restlet.data.MediaType;
import org.restlet.representation.Variant;
import org.restlet.resource.ServerResource;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.server.Utils;

public class BaseResource extends ServerResource {
	protected SpreadsheetService service;
	protected FeedURLFactory factory;
	protected String phoneNo;

	public BaseResource() {
		service = new SpreadsheetService("Goddess");
		String token = Utils.getGoogleToken();
		if (token == null) {
			setExisting(false);
			return;
		}
		service.setAuthSubToken(token);
		factory = FeedURLFactory.getDefault();
		phoneNo = Utils.getPhoneNo();
		getVariants().add(new Variant(MediaType.ALL));
	}

	protected boolean isAuthorized(String studentId) {
		if (studentId == null) {
			return false;
		}
		Map<String, Student> studList = Utils.getStudList();
		if (studList == null) {
			return false;
		}
		return studList.containsKey(studentId);
	}
}
