package com.sagarius.goddess.server.resources.base;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.KeyFactory;
import com.sagarius.goddess.server.model.DomainMapping;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.ModelUtils;

public class MappingResource extends ServerResource {
	public MappingResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String domain = form.getFirstValue("domain");
		String schoolId = form.getFirstValue("school");
		DomainMapping mapping = new DomainMapping(KeyFactory.createKey(
				DomainMapping.class.getSimpleName(), domain));
		mapping.setSchoolId(KeyFactory.createKey(School.class.getSimpleName(),
				schoolId));
		ModelUtils.persistEntity(mapping);
		return new EmptyRepresentation();
	}

}
