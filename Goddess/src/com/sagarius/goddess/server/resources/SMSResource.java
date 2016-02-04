package com.sagarius.goddess.server.resources;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.sagarius.radix.client.model.enumerations.Status;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.common.collect.Maps;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.utils.Fields;
import com.sagarius.goddess.client.utils.Fields.Staff;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.message.MessageGateway;
import com.sagarius.goddess.server.message.MessageGateway.MessageResponse;
import com.sagarius.goddess.server.message.MessageGatewayFactory;
import com.sagarius.goddess.server.model.neuf.ShortMessage;
import com.sagarius.goddess.server.resources.base.PagedResource;
import com.sagarius.goddess.server.util.PMFactory;
import com.sagarius.goddess.server.util.VisualizationUtils;

public class SMSResource extends PagedResource<ShortMessage> {

	SpreadsheetService service = new SpreadsheetService("Goddess");

	public SMSResource() {
		resourceName = "sms";
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Utils.initializeSchool();
		MessageGateway gateway = MessageGatewayFactory.getGateway();
		try {
			Request request = getRequest();
			Form form = request.getResourceRef().getQueryAsForm();
			String numbers = form.getFirstValue("no");
			String text = entity.getText();
			Key schoolId = Utils.getSchoolId();
			if (numbers != null) {
				String senderId = Utils.getSenderId();
				MessageResponse status = gateway.send(text, numbers, senderId);
				ShortMessage messageRoot = new ShortMessage();
				messageRoot.setMessage(text);
				messageRoot.setGroup("Anonymous");
				messageRoot.setMeta(Utils.getMetadata());
				if (status.isHasSucceeded()) {
					messageRoot.getMeta().setStatus(Status.SUCCESS);
				} else {
					messageRoot.getMeta().setStatus(Status.FAILED);
				}
				messageRoot.setParent(schoolId);
				messageRoot = com.sagarius.goddess.server.util.ModelUtils
						.persistEntity(messageRoot);

				Utils.saveMessages(numbers, status.getStatus(), messageRoot);
				return new EmptyRepresentation();
			}

			HttpSession session = ServletUtils.getRequest(request).getSession();
			String type = form.getFirstValue("type");
			MemberType memberType;
			if (type == null) {
				memberType = MemberType.STAFF;
			} else {
				memberType = MemberType.valueOf(type);
			}
			String gToken = (String) session.getAttribute("gToken");
			service.setAuthSubToken(gToken);

			String tqQuery = form.getFirstValue("tq");
			String group = form.getFirstValue("g");
			String relVal = form.getFirstValue("rel");

			ShortMessage message = new ShortMessage();
			message.setMessage(text);
			message.setGroup(group);
			message.setMeta(Utils.getMetadata());
			message.getMeta().setStatus(Status.IN_ACTIVE);
			message.setParent(schoolId);
			message = com.sagarius.goddess.server.util.ModelUtils
					.persistEntity(message);

			if (memberType == MemberType.STAFF && relVal != null) {
				sendMessageByRelation(tqQuery, text, message);
			} else {
				sendMessage(text, tqQuery, memberType, gToken, message);
			}
			return new EmptyRepresentation();
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(
					org.restlet.data.Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	private void sendMessageByRelation(String tqQuery, String text,
			ShortMessage message) throws Exception {
		String staffKey = Utils.getSheetKeyByType(DocumentType.STAFF);
		URL staffWorkUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(staffKey,
				"private", "values");
		URL staffListUrl = service.getFeed(staffWorkUrl, WorksheetFeed.class)
				.getEntries().get(0).getListFeedUrl();
		List<ListEntry> phoneEntries = service.getFeed(staffListUrl,
				ListFeed.class).getEntries();
		Map<String, String> phoneMap = new HashMap<String, String>();
		for (ListEntry phoneEntry : phoneEntries) {
			CustomElementCollection elements = phoneEntry.getCustomElements();
			String phone = elements.getValue(Fields.Generic.MOBILE);
			if (phone != null) {
				phoneMap.put(elements.getValue("staffid"), phone);
			}
		}

		String cssRelKey = Utils.getSheetKeyByType(DocumentType.CSSREL);
		URL cssWorkUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(cssRelKey,
				"private", "values");
		URL cssListUrl = service.getFeed(cssWorkUrl, WorksheetFeed.class)
				.getEntries().get(0).getListFeedUrl();
		ListQuery listQuery = VisualizationUtils.getListQuery(tqQuery,
				cssListUrl);
		List<ListEntry> cssEntries = service.getFeed(listQuery, ListFeed.class)
				.getEntries();
		Set<String> toSentStaffNos = new LinkedHashSet<String>();
		for (ListEntry cssEntry : cssEntries) {
			String staffNo = phoneMap.get(cssEntry.getCustomElements()
					.getValue(Staff.STAFF_ID));
			if (staffNo != null) {
				toSentStaffNos.add(staffNo);
			}
		}
		MessageGateway gateway = MessageGatewayFactory.getGateway();
		if (!toSentStaffNos.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (String staffNo : toSentStaffNos) {
				builder.append(staffNo).append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
			String toVal = builder.toString();
			String senderId = Utils.getSenderId();
			MessageResponse messageResponse = gateway.send(text, toVal,
					senderId);
			Utils.saveMessages(toVal, messageResponse.getStatus(), message);
			if (messageResponse.isHasSucceeded()) {
				message.getMeta().setStatus(Status.SUCCESS);
			} else {
				message.getMeta().setStatus(Status.FAILED);
			}
			com.sagarius.goddess.server.util.ModelUtils.persistEntity(message);
		}
	}

	private void sendMessage(String message, String tqQuery, MemberType type,
			String gToken, ShortMessage sms) throws Exception {
		String docKey = (type == MemberType.PARENT) ? Utils
				.getSheetKeyByType(DocumentType.SINGLE_STUDENT) : Utils
				.getSheetKeyByType(DocumentType.STAFF);
		URL worksheetFeedUrl = Utils.URL_FACTORY.getWorksheetFeedUrl(docKey,
				"private", "values");
		URL listFeedUrl = service
				.getFeed(worksheetFeedUrl, WorksheetFeed.class).getEntries()
				.get(0).getListFeedUrl();
		ListQuery listQuery = VisualizationUtils.getListQuery(tqQuery,
				listFeedUrl);
		String spreadsheetQuery = listQuery.getSpreadsheetQuery();
		String phoneField = type == MemberType.PARENT ? "parentsmobile"
				: "mobile";
		if (spreadsheetQuery != null) {
			listQuery.setSpreadsheetQuery(spreadsheetQuery + " and "
					+ phoneField + ">0");
		} else {
			listQuery.setSpreadsheetQuery(phoneField);
		}
		listQuery.setMaxResults(0);
		System.out.println(listQuery.getUrl().toString());

		ListFeed listFeed = service.getFeed(listQuery, ListFeed.class);
		int totalResults = listFeed.getTotalResults();
		Queue queue = QueueFactory.getQueue("smsqueue");
		LinkedHashMap<Object, Object> mainMap = Maps.newLinkedHashMap();
		mainMap.put("phoneField", phoneField);
		mainMap.put("gToken", gToken);
		mainMap.put("entity", message);
		mainMap.put("parent", KeyFactory.keyToString(sms.getRefId()));
		for (int i = 1; i <= totalResults; i += 75) {
			listQuery.setStartIndex(i);
			listQuery.setMaxResults(100);
			mainMap.put("queryUrl", listQuery.getUrl().toString());
			String payload = JSONObject.toJSONString(mainMap);
			queue.add(url("/qsms").payload(payload).method(Method.POST)
					.countdownMillis(1000));
		}
		sms.getMeta().setStatus(Status.ACTIVE);
		com.sagarius.goddess.server.util.ModelUtils.persistEntity(sms);
	}

	@Override
	protected Query getResourceQuery() {
		Query query = PMFactory.getManager().newQuery(ShortMessage.class);
		query.setFilter("meta.schoolId==schoolParam  && id==null");
		query.declareParameters("com.google.appengine.api.datastore.Key schoolParam");
		query.setOrdering("meta.timestamp desc");
		return query;
	}

	@Override
	protected Object[] getQueryParameters() {
		return new Object[] { Utils.getSchoolId() };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		String parent = getRequest().getResourceRef().getQueryAsForm()
				.getFirstValue("parent");
		if (parent == null) {
			return super.get(variant);
		}
		Query query = PMFactory.getManager().newQuery(ShortMessage.class);
		query.setFilter("parent==parentParam");
		query.declareParameters("com.google.appengine.api.datastore.Key parentParam");
		query.setOrdering("meta.timestamp desc");
		List<ShortMessage> entities = null;
		entities = (List<ShortMessage>) query.execute(KeyFactory
				.stringToKey(parent));
		if (entities == null) {
			return new StringRepresentation("[]");
		}
		int length = entities.size() - 1;
		String[] ids = new String[length];
		for (int i = 0; i < length; i++) {
			ids[i] = entities.get(i).getId();
		}
		ids = MessageGatewayFactory.getGateway().getStatus(ids);
		JSONArray result = new JSONArray();
		for (int i = 0; i < length; i++) {
			ShortMessage entity = entities.get(i);
			entity.setId(ids[i]);
			result.add(entity);
		}
		StringRepresentation rep = new StringRepresentation(
				result.toJSONString());
		rep.setMediaType(MediaType.APPLICATION_JSON);
		return rep;
	}

}
