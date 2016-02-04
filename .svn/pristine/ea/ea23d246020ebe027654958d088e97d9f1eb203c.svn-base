package com.sagarius.goddess.server.resources;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.net.URL;
import java.util.LinkedHashMap;

import org.json.simple.JSONArray;
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
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;

public class AttendanceResource extends ServerResource {
	public AttendanceResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setProtocolVersion(SpreadsheetService.Versions.V1);
			String googleToken = Utils.getGoogleToken();
			service.setAuthSubToken(googleToken);

			String jsonString = entity.getText();
			JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
			LinkedHashMap<Object, Object> mainMap = Maps.newLinkedHashMap();
			JSONArray forClazz = (JSONArray) jsonObject.get("clazz");
			int i = 0;
			Queue queue = QueueFactory.getQueue("attqueue");
			String studKey = Utils
					.getSheetKeyByType(DocumentType.SINGLE_STUDENT);
			URL studWorkUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(studKey,
					"private", "full");
			URL studListUrl = service.getFeed(studWorkUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			mainMap.put("token", googleToken);
			mainMap.put("studentListUrl", studListUrl.toString());
			mainMap.put("academicYear",
					KeyFactory.keyToString(Utils.getAcademicYear()));
			for (Object object : forClazz) {
				mainMap.put("clazz", object);
				queue.add(url("/qattendance")
						.payload(JSONObject.toJSONString(mainMap))
						.method(Method.POST).countdownMillis(i * 100000));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		return new EmptyRepresentation();
	}
}
