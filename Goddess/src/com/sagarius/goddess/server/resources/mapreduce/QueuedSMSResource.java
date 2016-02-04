package com.sagarius.goddess.server.resources.mapreduce;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.message.MessageGateway;
import com.sagarius.goddess.server.message.MessageGateway.MessageResponse;
import com.sagarius.goddess.server.message.MessageGatewayFactory;
import com.sagarius.goddess.server.model.neuf.ShortMessage;
import com.sagarius.goddess.server.util.PMFactory;

public class QueuedSMSResource extends ServerResource {
	SpreadsheetService service = new SpreadsheetService("Goddess");

	public QueuedSMSResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation representation, Variant variant)
			throws ResourceException {
		try {
			String repText = representation.getText();
			JSONObject mainMap = (JSONObject) new JSONParser().parse(repText);
			String text = (String) mainMap.get("entity");
			String gToken = (String) mainMap.get("gToken");
			String phoneField = (String) mainMap.get("phoneField");
			String queryUrl = (String) mainMap.get("queryUrl");
			Key parentKey = KeyFactory.stringToKey((String) mainMap
					.get("parent"));
			ShortMessage parentMessage = PMFactory.getManager().getObjectById(
					ShortMessage.class, parentKey);

			service.setAuthSubToken(gToken);
			ListQuery query = new ListQuery(new URL(queryUrl));
			ListFeed listFeed = service.getFeed(query, ListFeed.class);
			List<ListEntry> entries = listFeed.getEntries();
			Set<String> numbers = new LinkedHashSet<String>();
			for (ListEntry entry : entries) {
				String phoneNumber = entry.getCustomElements().getValue(
						phoneField);
				if (StringUtils.isNotBlank(phoneNumber)) {
					numbers.add(phoneNumber);
				}
			}
			if (!numbers.isEmpty()) {
				MessageGateway gateway = MessageGatewayFactory.getGateway();
				StringBuffer toBuffer = new StringBuffer();
				for (String number : numbers) {
					toBuffer.append(number + ",");
				}
				toBuffer.deleteCharAt(toBuffer.length() - 1);
				String receiver = toBuffer.toString();
				String senderId = Utils.getSenderId();
				MessageResponse messageResponse = gateway.send(text, receiver,
						senderId);
				com.sagarius.goddess.server.Utils.saveMessages(receiver,
						messageResponse.getStatus(), parentMessage);
			}
			return new EmptyRepresentation();
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

}
