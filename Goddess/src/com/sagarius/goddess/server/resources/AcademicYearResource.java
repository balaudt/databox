package com.sagarius.goddess.server.resources;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions.Method;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.FolderEntry;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.AcademicYear;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class AcademicYearResource extends ServerResource {
	public AcademicYearResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		try {
			Form form = new Form(entity.getText());
			String yearName = getRequest().getResourceRef().getQueryAsForm()
					.getFirstValue("name");
			String folderId;
			String token;
			Metadata metadata = null;
			if (StringUtils.isEmpty(yearName)) {
				folderId = form.getFirstValue("folderId");
				token = form.getFirstValue("token");
				yearName = form.getFirstValue("name");
				metadata = new Metadata();
				metadata.setSchoolId(KeyFactory.createKey(
						School.class.getSimpleName(), form.getFirstValue("id")));
				metadata.setTimestamp(new Date());
			} else {
				folderId = PMFactory.getManager()
						.getObjectById(School.class, Utils.getSchoolId())
						.getFolderId();
				token = Utils.getGoogleToken();
				metadata = Utils.getMetadata();
			}

			DocsService service = new DocsService("Goddess");
			service.setAuthSubToken(token);
			URL folderEntryUrl = new URL(
					"https://docs.google.com/feeds/default/private/full/folder%3A"
							.concat(folderId).concat("/contents"));

			DocumentListEntry newFolderEntry = new FolderEntry();
			newFolderEntry.setTitle(new PlainTextConstruct(yearName));
			newFolderEntry = service.insert(folderEntryUrl, newFolderEntry);
			URL newFolderEntryUrl = new URL(
					"https://docs.google.com/feeds/default/private/full/folder%3A"
							.concat(newFolderEntry.getDocId()).concat(
									"/contents"));

			AcademicYear newYear = new AcademicYear();
			newYear.setMetadata(metadata);
			newYear.setDisplayString(yearName);
			newYear.setFolderId(newFolderEntry.getDocId());
			newYear.setDefault(false);
			newYear = ModelUtils.persistEntity(newYear);

			ImmutableSet<DocumentType> typesGen = new ImmutableSet.Builder<DocumentType>()
					.add(DocumentType.STAFF).add(DocumentType.SINGLE_STUDENT)
					.add(DocumentType.CLASS).add(DocumentType.CSSREL)
					.add(DocumentType.DUMMY).build();
			Queue queue = QueueFactory.getDefaultQueue();
			int i = 0;
			LinkedHashMap<Object, Object> map = Maps.newLinkedHashMap();
			map.put("gtoken", token);
			map.put("yearName", yearName);
			map.put("yearDocUrl", newFolderEntryUrl.toString());
			map.put("yearRefId", KeyFactory.keyToString(newYear.getRefId()));
			for (DocumentType type : typesGen) {
				map.put("docType", type.toString());
				queue.add(url("/qyear").countdownMillis(i * 2000)
						.method(Method.POST)
						.payload(JSONObject.toJSONString(map)));
				i++;
			}

			return new StringRepresentation(newFolderEntry.getLink(
					Link.Rel.ALTERNATE, Link.Type.HTML).getHref());
		} catch (Exception e) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			e.printStackTrace();
			return new StringRepresentation(e.getMessage());
		}
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		String yearKeyString = getRequest().getResourceRef().getQueryAsForm()
				.getFirstValue("key");
		if (StringUtils.isEmpty(yearKeyString)) {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		PersistenceManager manager = PMFactory.getManager();
		Key yearKey = KeyFactory.stringToKey(yearKeyString);
		AcademicYear nextYear = manager.getObjectById(AcademicYear.class,
				yearKey);
		AcademicYear currentYear = manager.getObjectById(AcademicYear.class,
				Utils.getAcademicYear());
		ServletUtils.getRequest(Request.getCurrent()).getSession()
				.setAttribute("currentYear", yearKey);
		currentYear.setDefault(false);
		ModelUtils.persistEntity(currentYear);
		nextYear.setDefault(true);
		ModelUtils.persistEntity(nextYear);
		return new EmptyRepresentation();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Utils.initializeSchool();
		PersistenceManager manager = PMFactory.getManager();
		Query query = manager.newQuery(AcademicYear.class);
		query.setFilter("metadata.schoolId==schoolParam");
		query.declareParameters("com.google.appengine.api.datastore.Key schoolParam");
		Collection<AcademicYear> years = (Collection<AcademicYear>) query
				.execute(Utils.getSchoolId());
		JSONArray result = new JSONArray();
		for (AcademicYear year : years) {
			JSONArray singleYear = new JSONArray();
			singleYear.add(KeyFactory.keyToString(year.getRefId()));
			singleYear.add(year.getDisplayString());
			singleYear.add(year.isDefault());
			result.add(singleYear);
		}
		return new StringRepresentation(result.toJSONString(),
				MediaType.APPLICATION_JSON);
	}
}
