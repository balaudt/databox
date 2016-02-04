package com.sagarius.goddess.server.resources;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.sagarius.radix.client.model.enumerations.Status;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.KeyFactory;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.MessageTemplate;
import com.sagarius.goddess.server.resources.base.PagedResource;
import com.sagarius.goddess.server.util.PMFactory;

public class MessageTemplateResource extends PagedResource<MessageTemplate> {
	public MessageTemplateResource() {
		resourceName = "mtemplate";
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String key = form.getFirstValue("key");
		PersistenceManager manager = PMFactory.getManager();
		MessageTemplate messageTemplate = manager.getObjectById(
				MessageTemplate.class, KeyFactory.stringToKey(key));
		manager.deletePersistent(messageTemplate);
		getResponse().setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
		return new EmptyRepresentation();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		String map = getRequest().getResourceRef().getQueryAsForm()
				.getFirstValue("map");
		if (StringUtils.isEmpty(map)) {
			return super.get(variant);
		}
		Query query = getResourceQuery();
		List<MessageTemplate> entities = null;
		entities = (List<MessageTemplate>) query
				.executeWithArray(getQueryParameters());
		if (entities == null || entities.isEmpty()) {
			return new StringRepresentation("[]");
		}
		JSONArray result = new JSONArray();
		int size = entities.size();
		int i;
		for (i = 0; i < size; i++) {
			result.add(null);
		}
		i = 0;
		for (MessageTemplate messageTemplate : entities) {
			JSONObject template = new JSONObject();
			template.put("name", messageTemplate.getName());
			template.put("message", messageTemplate.getMessage());
			result.set(i++, template);
		}
		StringRepresentation rep = new StringRepresentation(
				result.toJSONString());
		rep.setMediaType(MediaType.APPLICATION_JSON);
		return rep;
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String name = form.getFirstValue("name");
		String message = form.getFirstValue("message");
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(message)) {
			getResponse().setStatus(
					org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		MessageTemplate messageTemplate = new MessageTemplate();
		messageTemplate.setName(name);
		messageTemplate.setMessage(message);
		messageTemplate.setMeta(Utils.getMetadata());
		messageTemplate = com.sagarius.goddess.server.util.ModelUtils
				.persistEntity(messageTemplate);
		return new StringRepresentation(messageTemplate.render());
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String name = form.getFirstValue("name");
		String message = form.getFirstValue("message");
		String key = form.getFirstValue("key");
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(message)
				|| StringUtils.isEmpty(key)) {
			getResponse().setStatus(
					org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		MessageTemplate messageTemplate = PMFactory.getManager().getObjectById(
				MessageTemplate.class, KeyFactory.stringToKey(key));
		messageTemplate.setName(name);
		messageTemplate.setMessage(message);
		Metadata meta = messageTemplate.getMeta();
		meta.setEditor(Utils.getCurrentMember());
		meta.setTimestamp(new Date());
		messageTemplate = com.sagarius.goddess.server.util.ModelUtils
				.persistEntity(messageTemplate);
		return new StringRepresentation(messageTemplate.render());
	}

	@Override
	protected Query getResourceQuery() {
		Query query = PMFactory.getManager().newQuery(MessageTemplate.class);
		query.setFilter("meta.schoolId==schoolParam && meta.status==statusParam");
		query.declareParameters("com.google.appengine.api.datastore.Key schoolParam, "
				+ "org.sagarius.radix.client.model.enumerations.Status statusParam");
		query.setOrdering("refId desc");
		return query;
	}

	@Override
	protected Object[] getQueryParameters() {
		return new Object[] { Utils.getSchoolId(), Status.ACTIVE };
	}

}
