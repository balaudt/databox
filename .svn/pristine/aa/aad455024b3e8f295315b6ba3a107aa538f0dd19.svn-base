package com.sagarius.goddess.server.resources.parent;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;

public class StudentResource extends BaseResource {

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			String studentId = (String) getRequest().getAttributes().get(
					"studentId");
			if (studentId == null) {
				Map<String, Student> studList = Utils.getStudList();
				Set<Entry<String, Student>> studEntries = studList.entrySet();
				JSONArray output = new JSONArray();
				for (Entry<String, Student> studEntry : studEntries) {
					output.add(studEntry.getValue().getName() + "-"
							+ studEntry.getKey());
				}
				return new StringRepresentation(output.toJSONString());
			}
			boolean authorized = isAuthorized(studentId);
			if (!authorized) {
				getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return null;
			}
			URL studWorkUrl = factory.getWorksheetFeedUrl(
					Utils.getSheetKeyByType(DocumentType.SINGLE_STUDENT),
					"private", "values");
			URL studListUrl = service.getFeed(studWorkUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			ListQuery studListQuery = new ListQuery(studListUrl);
			studListQuery
					.setSpreadsheetQuery("studentid=\"" + studentId + "\"");
			ListEntry studEntry = service
					.getFeed(studListQuery, ListFeed.class).getEntries().get(0);
			CustomElementCollection elements = studEntry.getCustomElements();
			Set<String> tags = elements.getTags();
			JSONObject output = new JSONObject();
			for (String tag : tags) {
				output.put(tag, elements.getValue(tag));
			}
			return new StringRepresentation(output.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
}
