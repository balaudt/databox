package com.sagarius.goddess.server.resources.parent;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.server.Utils;

public class ExamResource extends BaseResource {
	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		String studentId = (String) getRequest().getAttributes().get(
				"studentId");
		String docId = (String) getRequest().getAttributes().get("docId");
		if (studentId == null || docId == null) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		if (!isAuthorized(studentId)) {
			getResponse().setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}
		Map<String, Student> studList = Utils.getStudList();
		Student student = studList.get(studentId);
		String clazz = student.getClazz();
		try {
			URL examWorkUrl = factory.getWorksheetFeedUrl(docId, "private",
					"values");
			WorksheetQuery workQuery = new WorksheetQuery(examWorkUrl);
			workQuery.setTitleExact(true);
			workQuery.setTitleQuery(clazz);
			WorksheetFeed examWorkFeed = service.getFeed(workQuery,
					WorksheetFeed.class);
			if (examWorkFeed.getTotalResults() <= 0) {
				return new StringRepresentation("[]");
			}
			URL examListUrl = examWorkFeed.getEntries().get(0).getListFeedUrl();
			ListQuery examQuery = new ListQuery(examListUrl);
			StringBuilder builder = new StringBuilder();
			String[] reqRows = new String[] { "Minimum Marks", "Maximum Marks",
					studentId, "Average", "CL1", "CL2", "CL3" };
			for (String reqRow : reqRows) {
				builder.append("studentid=\"" + reqRow + "\" or ");
			}
			int length = builder.length();
			builder.delete(length - 4, length);
			examQuery.setSpreadsheetQuery(builder.toString());

			JSONArray output = new JSONArray();

			List<ListEntry> entries = service
					.getFeed(examQuery, ListFeed.class).getEntries();
			String rank = null;
			String percentage = null;
			String total = null;
			String maxTotal = null;
			String result = null;
			ListEntry minMarkEntry = null;
			ListEntry maxMarkEntry = null;
			ListEntry studMarkEntry = null;
			Set<String> subjList = null;
			final Map<String, String> realSubjectNames = new HashMap<String, String>();
			for (ListEntry examEntry : entries) {
				CustomElementCollection listEntry = examEntry
						.getCustomElements();
				String admNo = listEntry.getValue("studentid");
				if (admNo.equals("Minimum Marks")) {
					minMarkEntry = examEntry;
				} else if (admNo.equals("Maximum Marks")) {
					maxMarkEntry = examEntry;
					maxTotal = listEntry.getValue("total");
				} else if (admNo.equals(studentId)) {
					rank = listEntry.getValue("rank");
					percentage = listEntry.getValue("percentage");
					total = listEntry.getValue("total");
					result = listEntry.getValue("passed");
					studMarkEntry = examEntry;
					subjList = listEntry.getTags();
					subjList.remove("studentid");
					subjList.remove("name");
					subjList.remove("total");
					subjList.remove("passed");
					subjList.remove("percentage");
					subjList.remove("rank");
					subjList.remove("result");
				} else if (admNo.startsWith("CL")) {
					Set<String> tags = examEntry.getCustomElements().getTags();
					for (String tag : tags) {
						String value = listEntry.getValue(tag);
						if (tag.startsWith("_") && !StringUtils.isEmpty(value)) {
							realSubjectNames.put(tag, value);
						}
					}
				}
			}
			for (String subject : subjList) {
				JSONArray singleSubject = new JSONArray();
				if (subject == null) {
					continue;
				}
				if (!subject.startsWith("_")) {
					singleSubject.add(subject);
				} else {
					singleSubject.add(realSubjectNames.get(subject));
				}
				singleSubject.add(minMarkEntry.getCustomElements().getValue(
						subject));
				singleSubject.add(maxMarkEntry.getCustomElements().getValue(
						subject));
				singleSubject.add(studMarkEntry.getCustomElements().getValue(
						subject));
				output.add(singleSubject);
			}
			JSONArray consolidation = new JSONArray();
			consolidation.add(rank);
			consolidation.add(percentage);
			consolidation.add(total);
			consolidation.add(maxTotal);
			consolidation.add(result);
			output.add(consolidation);
			return new StringRepresentation(output.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
}
