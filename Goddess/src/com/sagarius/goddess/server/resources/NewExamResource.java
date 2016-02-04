package com.sagarius.goddess.server.resources;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.common.collect.Maps;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.util.DocumentUtils;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class NewExamResource extends ServerResource {
	public NewExamResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String cccKey = form.getFirstValue("ccc");
		if (StringUtils.isEmpty(cccKey)) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		Key id = KeyFactory.createKey(Document.class.getSimpleName(), cccKey);
		PersistenceManager manager = PMFactory.getManager();
		Document document = manager.getObjectById(Document.class, id);
		Transaction transaction = manager.currentTransaction();
		try {
			transaction.begin();
			manager.deletePersistent(document);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
		return new EmptyRepresentation();
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			// Parsing the request and initializing
			String jsonString = entity.getText();
			JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
			String examName = (String) jsonObject.get("name");
			String googleToken = Utils.getGoogleToken();
			SpreadsheetEntry document = (SpreadsheetEntry) DocumentUtils.createEmptySheet(
					examName, googleToken);
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(googleToken);

			// Creating the Consolidated sheet to be updated by the threads
			// spawned below
			URL worksheetFeedUrl = document.getWorksheetFeedUrl();// document.getWorksheets()
			WorksheetEntry worksheetEntry = service
					.getFeed(worksheetFeedUrl, WorksheetFeed.class)
					.getEntries().get(0);
			worksheetEntry.setRowCount(1);
			URL editUrl = new URL(worksheetEntry.getLink(Link.Rel.ENTRY_EDIT,
					Link.Type.ATOM).getHref());
			String headers[] = new String[] { "Standard", "Section", "Subject",
					"CL1", "CL2", "CL3", "Staff", "Minimum Marks",
					"Maximum Marks", "Highest", "Average", "No of Pass",
					"No of Fail", "No of abs", "Above 90", "Attendants" };
			worksheetEntry.setColCount(headers.length);
			worksheetEntry.setTitle(new PlainTextConstruct("Consolidated"));
			service.update(editUrl, worksheetEntry);
			URL cellFeedUrl = worksheetEntry.getCellFeedUrl();
			CellQuery cellQuery = new CellQuery(cellFeedUrl);
			cellQuery.setReturnEmpty(true);
			CellFeed inFeed = service.getFeed(cellQuery, CellFeed.class);
			CellFeed outFeed = new CellFeed();
			List<CellEntry> inEntries = inFeed.getEntries();

			int i = 0;
			for (String header : headers) {
				Utils.helpBatch(inEntries.get(i++), outFeed, header);
			}
			String cellOutUrl = inFeed.getLink(Link.Rel.FEED_BATCH,
					Link.Type.ATOM).getHref();
			service.batch(new URL(cellOutUrl), outFeed);

			// Spawning requests to create class sheets
			Queue queue = QueueFactory.getQueue("examqueue");
			String studKey = Utils
					.getSheetKeyByType(DocumentType.SINGLE_STUDENT);
			URL studWorkUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(studKey,
					"private", "full");
			URL studListUrl = service.getFeed(studWorkUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			LinkedHashMap<Object, Object> mainMap = Maps.newLinkedHashMap();
			mainMap.put("token", googleToken);
			mainMap.put("studentListUrl", studListUrl.toString());
			mainMap.put("examWorkUrl", worksheetFeedUrl.toString());
			JSONArray selectedClasses = (JSONArray) jsonObject.get("clazz");
			i = 0;
			for (Object object : selectedClasses) {
				mainMap.put("clazz", object);
				queue.add(url("/nqexam")
						.payload(JSONObject.toJSONString(mainMap))
						.method(Method.POST).countdownMillis(i * 2000));
				i++;
			}

			// Persist document and return the link to the document
			String id = com.sagarius.goddess.server.Utils.parseId(
					document.getResourceId(), ":");
			Document examDocument = new Document(KeyFactory.createKey(
					Document.class.getSimpleName(), id));
			examDocument.setCccKey(id);
			examDocument.setAcademicYear(Utils.getAcademicYear());
			examDocument.setType(DocumentType.EXAMINATION);
			examDocument.setDocTitle(examName);
			examDocument.setDocName(examName);
			examDocument.setParam1(examName);
			ModelUtils.persistEntity(examDocument);

			setStatus(Status.SUCCESS_CREATED);
			return new StringRepresentation(document.getHtmlLink().getHref()
					.toString());
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String isGenerate = form.getFirstValue("generate");
		if (isGenerate == null) {
			// Update the exam meta data
			return new EmptyRepresentation();
		}
		SpreadsheetService service = new SpreadsheetService("Goddess");
		String googleToken = Utils.getGoogleToken();
		service.setProtocolVersion(SpreadsheetService.Versions.V1);
		service.setAuthSubToken(googleToken);
		Queue queue = QueueFactory.getQueue("examqueue");
		LinkedHashMap<Object, Object> mainMap = Maps.newLinkedHashMap();
		mainMap.put("token", googleToken);
		try {
			String cssRel = Utils.getSheetKeyByType(DocumentType.CSSREL);
			URL cssUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(cssRel,
					"private", "full");
			WorksheetEntry cssEntry = service
					.getFeed(cssUrl, WorksheetFeed.class).getEntries().get(0);
			mainMap.put("cssEntry", cssEntry.getSelfLink().getHref());

			String docKey = form.getFirstValue("ccc");
			URL worksheetFeedUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(
					docKey, "private", "full");
			List<WorksheetEntry> workEntries = service.getFeed(
					worksheetFeedUrl, WorksheetFeed.class).getEntries();
			for (WorksheetEntry worksheetEntry : workEntries) {
				if (worksheetEntry.getTitle().getPlainText()
						.equals("Consolidated")) {
					mainMap.put("consolidatedEntry", worksheetEntry
							.getSelfLink().getHref());
					break;
				}
			}
			int i = 0;
			for (WorksheetEntry worksheetEntry : workEntries) {
				if (worksheetEntry.getTitle().getPlainText()
						.equals("Consolidated")) {
					continue;
				}
				mainMap.put("workEntry", worksheetEntry.getSelfLink().getHref());
				queue.add(url("/nqexam")
						.payload(JSONObject.toJSONString(mainMap))
						.method(Method.PUT).countdownMillis(10000 * i));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new StringRepresentation(e.getMessage());
		}
		return new StringRepresentation("Process initiated");
	}
}
