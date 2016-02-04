package com.sagarius.goddess.server.resources;

import java.util.Collection;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.sagarius.radix.server.model.util.BaseEntry;
import org.sagarius.radix.server.model.util.EntryCollection;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.PMFactory;

public class DocumentResource extends ServerResource {
	public DocumentResource() {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Utils.initializeSchool();
		PersistenceManager manager = PMFactory.getManager();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String cccKey = form.getFirstValue("ccc");
		Query query = manager.newQuery(Document.class);
		if (!StringUtils.isEmpty(cccKey)) {
			query.setFilter("cccKey==keyParam");
			query.declareParameters("String keyParam");
			Collection<Document> documents = (Collection<Document>) query
					.execute(cccKey);
			if (documents == null || documents.isEmpty()) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}
			Document document = documents.iterator().next();
			return new StringRepresentation(document.render(),
					MediaType.APPLICATION_JSON);
		}
		String inDom = form.getFirstValue("domain");
		EntryCollection resultDocuments = new EntryCollection();
		if (inDom != null) {
			HttpSession session = ServletUtils.getRequest(getRequest())
					.getSession();
			Key schoolKey = (Key) session.getAttribute("school");
			School school = PMFactory.getManager().getObjectById(School.class,
					schoolKey);
			BaseEntry entry = new BaseEntry();
			entry.set(0, school.getDomainName());
			resultDocuments.add(entry);
		}
		String yearKey = form.getFirstValue("year");
		Key academicKey = null;
		if (StringUtils.isEmpty(yearKey)) {
			academicKey = (Key) ServletUtils.getRequest(getRequest())
					.getSession().getAttribute("currentYear");
		} else {
			academicKey = KeyFactory.stringToKey(yearKey);
		}
		String docType = form.getFirstValue("type");
		query.setFilter("academicYear==yearParam && type==typeParam");
		query.declareParameters("com.google.appengine.api.datastore.Key yearParam, "
				+ "com.sagarius.goddess.client.model.enumerations.DocumentType typeParam");
		Collection<Document> documents = null;
		if (docType != null) {
			DocumentType type = DocumentType.valueOf(docType);
			documents = (Collection<Document>) query.execute(academicKey, type);
			for (Document document : documents) {
				resultDocuments.add(document);
			}
		} else {
			documents = (Collection<Document>) query.execute(academicKey,
					DocumentType.STAFF);
			if (documents != null && !documents.isEmpty()) {
				resultDocuments.add(documents.iterator().next());
			}
			documents = (Collection<Document>) query.execute(academicKey,
					DocumentType.SINGLE_STUDENT);
			if (documents != null && !documents.isEmpty()) {
				resultDocuments.add(documents.iterator().next());
			}
			documents = (Collection<Document>) query.execute(academicKey,
					DocumentType.CLASS);
			if (documents != null && !documents.isEmpty()) {
				resultDocuments.add(documents.iterator().next());
			}
			documents = (Collection<Document>) query.execute(academicKey,
					DocumentType.CSSREL);
			if (documents != null && !documents.isEmpty()) {
				resultDocuments.add(documents.iterator().next());
			}
			documents = (Collection<Document>) query.execute(academicKey,
					DocumentType.DUMMY);
			if (documents != null && !documents.isEmpty()) {
				resultDocuments.add(documents.iterator().next());
			}
		}
		return new StringRepresentation(resultDocuments.render(),
				MediaType.APPLICATION_JSON);
	}
}
