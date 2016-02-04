package com.sagarius.goddess.server.resources.parent;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.simple.JSONArray;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.WorksheetQuery;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.util.PMFactory;

public class AttendanceResource extends BaseResource {
	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		String studentId = (String) getRequest().getAttributes().get(
				"studentId");
		if (!isAuthorized(studentId)) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		String clazz = Utils.getStudList().get(studentId).getClazz();
		PersistenceManager manager = PMFactory.getManager();
		Query query = manager.newQuery(Document.class);
		query.setFilter("academicYear==yearParam && type==typeParam && param1==clazzParam");
		query.declareParameters("com.google.appengine.api.datastore.Key yearParam, "
				+ "com.sagarius.goddess.client.model.enumerations.DocumentType typeParam,String clazzParam");
		Collection<Document> documents = (Collection<Document>) query.execute(
				Utils.getAcademicYear(), DocumentType.ATTENDANCE, clazz);
		if (documents == null || documents.isEmpty()) {
			return new StringRepresentation("[]");
		}
		try {
			String docId = documents.iterator().next().getDocumentKey()
					.getName();
			URL examWorkUrl = factory.getWorksheetFeedUrl(docId, "private",
					"values");
			WorksheetQuery workQuery = new WorksheetQuery(examWorkUrl);
			workQuery.setTitleExact(true);
			workQuery.setTitleQuery("Consolidated");
			WorksheetFeed attendWorkFeed = service.getFeed(workQuery,
					WorksheetFeed.class);
			if (attendWorkFeed.getTotalResults() <= 0) {
				return new StringRepresentation("[]");
			}
			URL attendListUrl = attendWorkFeed.getEntries().get(0)
					.getListFeedUrl();
			ListQuery attendQuery = new ListQuery(attendListUrl);
			attendQuery
					.setSpreadsheetQuery("studentid=\"Working days\" or studentid=\""
							+ studentId + "\"");
			List<ListEntry> entries = service.getFeed(attendQuery,
					ListFeed.class).getEntries();
			ListEntry workEntry = entries.get(0);
			ListEntry studEntry = entries.get(1);
			Set<String> months = studEntry.getCustomElements().getTags();
			List<String> otherFields = Arrays.asList("studentid", "name",
					"total");
			JSONArray output = new JSONArray();
			for (String month : months) {
				if (otherFields.contains(month)) {
					continue;
				}
				JSONArray singleMonth = new JSONArray();
				singleMonth.add(month);
				singleMonth.add(workEntry.getCustomElements().getValue(month));
				singleMonth.add(studEntry.getCustomElements().getValue(month));
				output.add(singleMonth);
			}
			JSONArray consolidation = new JSONArray();
			consolidation.add(workEntry.getCustomElements().getValue("total"));
			consolidation.add(studEntry.getCustomElements().getValue("total"));
			output.add(consolidation);
			return new StringRepresentation(output.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
}
