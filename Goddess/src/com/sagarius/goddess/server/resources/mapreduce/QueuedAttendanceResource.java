package com.sagarius.goddess.server.resources.mapreduce;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.common.collect.Maps;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.util.DocumentUtils;
import com.sagarius.goddess.server.util.ModelUtils;

public class QueuedAttendanceResource extends ServerResource {
	public QueuedAttendanceResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	// TODO Can be optimized by sending the student id and name as a part of the
	// payload for each month generation instead of getting once again ids and
	// names
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			String jsonString = entity.getText();
			JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
			String forClazz = (String) jsonObject.get("clazz");
			String googleToken = (String) jsonObject.get("token");
			String academicYear = (String) jsonObject.get("academicYear");
			String studentListUrl = (String) jsonObject.get("studentListUrl");
			int year = new GregorianCalendar().get(Calendar.YEAR);
			SpreadsheetEntry sheetEntry = (SpreadsheetEntry) DocumentUtils
					.createEmptySheet(forClazz + " - Register", googleToken,
							KeyFactory.stringToKey(academicYear));
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			service.setAuthSubToken(googleToken);
			WorksheetEntry consolidateEntry = service
					.getFeed(sheetEntry.getWorksheetFeedUrl(),
							WorksheetFeed.class).getEntries().get(0);
			consolidateEntry.setTitle(new PlainTextConstruct("Consolidated"));
			consolidateEntry.update();

			LinkedHashMap<Object, Object> mainMap = Maps.newLinkedHashMap();
			mainMap.put("year", year);
			mainMap.put("token", googleToken);
			mainMap.put("clazz", forClazz);
			mainMap.put("studentListUrl", studentListUrl);
			mainMap.put("docWorkUrl", sheetEntry.getWorksheetFeedUrl()
					.toString());
			Queue queue = QueueFactory.getQueue("attqueue");
			int month;
			for (month = 0; month < 12; month++) {
				mainMap.put("month", month);
				queue.add(url("/qattendmonth")
						.payload(JSONObject.toJSONString(mainMap))
						.method(Method.POST).countdownMillis(month * 2000));
			}

			queue.add(url("/qattendconsolid")
					.payload(JSONObject.toJSONString(mainMap))
					.method(Method.POST).countdownMillis(2000 * month));

			String id = com.sagarius.goddess.server.Utils.parseId(
					sheetEntry.getResourceId(), ":");
			Document attendDocument = new Document(KeyFactory.createKey(
					Document.class.getSimpleName(), id));
			attendDocument
					.setAcademicYear(KeyFactory.stringToKey(academicYear));
			attendDocument.setParam1(forClazz);
			attendDocument.setCccKey(id);
			attendDocument.setType(DocumentType.ATTENDANCE);
			attendDocument.setDocTitle(forClazz + " - Register");
			ModelUtils.persistEntity(attendDocument);
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		return new EmptyRepresentation();
	}
}
