package com.sagarius.goddess.server.resources.base;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.Key;
import com.sagarius.goddess.client.model.enumerations.SchoolType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class SchoolResource extends ServerResource {
	public SchoolResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		try {
			Form form = new Form(entity.getText());
			String name = form.getFirstValue("name");
			String address = form.getFirstValue("address");
			String email = form.getFirstValue("mail");
			String phoneNo = form.getFirstValue("phone");
			String mobile = form.getFirstValue("mobile");
			String siteUrl = form.getFirstValue("site");
			String staffCount = form.getFirstValue("staff");
			String category = form.getFirstValue("category");
			String studentCount = form.getFirstValue("student");
			Key schoolId = Utils.getSchoolId();
			School school = PMFactory.getManager().getObjectById(School.class,
					schoolId);
			school.setName(name);
			school.setAddress(address);
			school.setEmail(email);
			school.setPhoneNo(phoneNo);
			school.setMobile(mobile);
			school.setSiteUrl(siteUrl);
			if (staffCount != null) {
				try {
					school.setStaffCount(Integer.parseInt(staffCount));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			if (studentCount != null) {
				try {
					school.setStudentCount(Integer.parseInt(studentCount));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			school.setType(SchoolType.valueOf(category));
			school = ModelUtils.persistEntity(school, true);
			return new StringRepresentation(school.render(),
					MediaType.TEXT_HTML);
		} catch (IOException e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Utils.initializeSchool();
		Key schoolId = Utils.getSchoolId();
		School school = PMFactory.getManager().getObjectById(School.class,
				schoolId);
		return new StringRepresentation(school.render(School.class),
				MediaType.APPLICATION_JSON);
	}
}
