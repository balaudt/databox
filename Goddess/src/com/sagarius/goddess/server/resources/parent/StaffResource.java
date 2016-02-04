package com.sagarius.goddess.server.resources.parent;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;

public class StaffResource extends BaseResource {
	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		String studentId = (String) getRequest().getAttributes().get(
				"studentId");
		if (studentId == null) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		boolean authorized = isAuthorized(studentId);
		if (!authorized) {
			getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}
		try {

			URL relWorkUrl = factory.getWorksheetFeedUrl(
					Utils.getSheetKeyByType(DocumentType.CSSREL), "private",
					"values");
			URL relListUrl = service.getFeed(relWorkUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			Map<String, Student> studList = Utils.getStudList();
			Student student = studList.get(studentId);
			String clazz = student.getClazz();
			String standard = clazz.split("-")[0];
			String section = clazz.split("-")[1];
			ListQuery relQuery = new ListQuery(relListUrl);
			relQuery.setSpreadsheetQuery("standard=\"" + standard
					+ "\" and section=\"" + section + "\"");
			List<ListEntry> relEntries = service.getFeed(relQuery,
					ListFeed.class).getEntries();
			for (ListEntry relEntry : relEntries) {
				JSONArray relation = new JSONArray();
				relation.add(relEntry.getCustomElements().getValue(""));
			}
			// TODO Incomplete
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
}
