package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.collect.ImmutableMap;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.utils.Fields;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.util.ModelUtils;

public class QueuedYearResource extends ServerResource {
	public QueuedYearResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			String repText = entity.getText();
			JSONObject mainMap = (JSONObject) new JSONParser().parse(repText);
			String gtoken = (String) mainMap.get("gtoken");
			String docType = (String) mainMap.get("docType");
			String yearName = (String) mainMap.get("yearName");
			String yearDocUrl = (String) mainMap.get("yearDocUrl");
			String yearRefId = (String) mainMap.get("yearRefId");

			DocsService docService = new DocsService("Goddess");
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			docService.setAuthSubToken(gtoken);
			service.setAuthSubToken(gtoken);

			ImmutableMap<DocumentType, String> nameMap = new ImmutableMap.Builder<DocumentType, String>()
					.put(DocumentType.STAFF, "Staff-list")
					.put(DocumentType.SINGLE_STUDENT, "Student-list")
					.put(DocumentType.CLASS, "Class-list")
					.put(DocumentType.CSSREL, "Relations")
					.put(DocumentType.DUMMY, "App Data").build();
			ImmutableMap<DocumentType, String[]> fieldMap = new ImmutableMap.Builder<DocumentType, String[]>()
					.put(DocumentType.STAFF, Fields.STAFF_META)
					.put(DocumentType.SINGLE_STUDENT, Fields.STUDENT_META)
					.put(DocumentType.CLASS, Fields.CLASS_META)
					.put(DocumentType.CSSREL, Fields.RELATIONS_META)
					.put(DocumentType.DUMMY, new String[] { "315" }).build();
			DocumentType type = DocumentType.valueOf(docType);
			String docName = nameMap.get(type);
			String[] fields = fieldMap.get(type);

			SpreadsheetEntry entry = new SpreadsheetEntry();
			entry.setTitle(new PlainTextConstruct(docName.concat("-").concat(
					yearName)));
			entry = docService.insert(new URL(yearDocUrl), entry);
			String docId = entry.getDocId();
			Document document = new Document(KeyFactory.createKey(
					Document.class.getSimpleName(), docId));
			document.setCccKey(docId);
			document.setAcademicYear(KeyFactory.stringToKey(yearRefId));
			document.setDocName(docName.concat("-").concat(yearName));
			document.setDocTitle(docName);
			document.setType(type);
			ModelUtils.persistEntity(document);

			FeedURLFactory factory = FeedURLFactory.getDefault();
			WorksheetEntry worksheetEntry = service
					.getFeed(
							factory.getWorksheetFeedUrl(docId, "private",
									"full"), WorksheetFeed.class).getEntries()
					.get(0);
			worksheetEntry.setColCount(fields.length);
			worksheetEntry.setTitle(new PlainTextConstruct(docName));
			worksheetEntry.update();
			CellQuery cellQuery = new CellQuery(worksheetEntry.getCellFeedUrl());
			cellQuery.setMinimumRow(1);
			cellQuery.setMaximumRow(1);
			cellQuery.setMinimumCol(1);
			cellQuery.setMaximumCol(fields.length);
			cellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			CellFeed outFeed = new CellFeed();
			List<CellEntry> cellEntries = inFeed.getEntries();
			for (int i = 0; i < fields.length; i++) {
				Utils.helpBatch(cellEntries.get(i), outFeed,
						fields[i].toUpperCase());
			}
			String cellOutUrl = inFeed.getLink(Link.Rel.FEED_BATCH,
					Link.Type.ATOM).getHref();
			outFeed = service.batch(new URL(cellOutUrl), outFeed);
			Utils.logBatchStatus(outFeed);
			return new EmptyRepresentation();
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}
}
