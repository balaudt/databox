package com.sagarius.goddess.server.resources.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Query;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.store.appengine.query.JDOCursorHelper;
import org.json.simple.JSONObject;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.Cursor;

public abstract class PagedResource<T> extends ServerResource {
	protected String resourceName;

	public PagedResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation head(Variant variant) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String cursorInfo = form.getFirstValue("cursor");
		Query query = getResourceQuery();
		Cursor cursor = Cursor.fromWebSafeString(cursorInfo);
		Map<String, Object> extensionMap = new HashMap<String, Object>();
		extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
		query.setExtensions(extensionMap);
		List<T> entities = null;
		if (getQueryParameters() != null) {
			entities = (List<T>) query.executeWithArray(getQueryParameters());
		} else {
			entities = (List<T>) query.execute();
		}
		if (entities == null || entities.isEmpty()) {
			getResponse().setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
		} else {
			getResponse().setStatus(org.restlet.data.Status.SUCCESS_OK);
		}
		return new EmptyRepresentation();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String cursorInfo = form.getFirstValue("cursor");
		Query query = getResourceQuery();
		query.setRange(0, 10);
		if (!StringUtils.isEmpty(cursorInfo)) {
			Cursor cursor = Cursor.fromWebSafeString(cursorInfo);
			Map<String, Object> extensionMap = new HashMap<String, Object>();
			extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
			query.setExtensions(extensionMap);
		}
		List<T> entities = null;
		if (getQueryParameters() != null) {
			entities = (List<T>) query.executeWithArray(getQueryParameters());
		} else {
			entities = (List<T>) query.execute();
		}
		if (entities == null || entities.isEmpty()) {
			return new StringRepresentation("{}");
		}
		Map<String, Object> results = new HashMap<String, Object>();
		results.put(resourceName, entities);
		results.put("cursor", JDOCursorHelper.getCursor(entities)
				.toWebSafeString());
		getResponse().setStatus(org.restlet.data.Status.SUCCESS_OK);
		StringRepresentation rep = new StringRepresentation(
				JSONObject.toJSONString(results));
		rep.setMediaType(MediaType.APPLICATION_JSON);
		return rep;
	}

	protected abstract Query getResourceQuery();

	protected abstract Object[] getQueryParameters();
}
