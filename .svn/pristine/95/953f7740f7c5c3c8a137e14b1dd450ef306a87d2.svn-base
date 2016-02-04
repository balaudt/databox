package com.sagarius.goddess.server.resources;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
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
import com.google.common.collect.ImmutableMap;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.docs.FolderEntry;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.model.enumerations.SchoolType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.DomainMapping;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.DocumentUtils;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class RegisterResource extends ServerResource {
	public RegisterResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		try {
			Reference resourceRef = getRequest().getResourceRef();
			Form form = resourceRef.getQueryAsForm();
			String id = form.getFirstValue("id");
			Key schoolId = KeyFactory.createKey(School.class.getSimpleName(),
					id);
			int step = Integer.parseInt(form.getFirstValue("step"));
			if (step == 1) {
				String token = form.getFirstValue("token");
				String sessionToken = AuthSubUtil.exchangeForSessionToken(
						token, null);
				Member admin = new Member();
				admin.setAuthToken(sessionToken);
				admin.setType(MemberType.ADMINISTRATOR);
				Metadata metadata = new Metadata();
				metadata.setSchoolId(schoolId);
				admin.setMetadata(metadata);
				admin.setEmail(form.getFirstValue("email"));
				ModelUtils.persistEntity(admin);
				String scope = "http://picasaweb.google.com/data/";
				Reference reference = new Reference(resourceRef.toString(false,
						false));
				reference.addQueryParameter("id", id);
				reference.addQueryParameter("step", "2");
				getResponse().redirectTemporary(
						AuthSubUtil.getRequestUrl(reference.toString(), scope,
								false, true));
				return null;
			} else if (step == 2) {
				String token = form.getFirstValue("token");
				String sessionToken = AuthSubUtil.exchangeForSessionToken(
						token, null);
				Query query = PMFactory.getManager().newQuery(Member.class);
				query.setFilter("type==typeParam && metadata.schoolId==idParam");
				query.declareParameters("com.sagarius.goddess.client.model.enumerations.MemberType typeParam,"
						+ "com.google.appengine.api.datastore.Key idParam");
				Member admin = ((Collection<Member>) query.execute(
						MemberType.ADMINISTRATOR, schoolId)).iterator().next();
				admin.setOtherFields(new ImmutableMap.Builder<String, String>()
						.put("picasa", sessionToken).build());
				ModelUtils.persistEntity(admin);
				getResponse().redirectSeeOther(
						"/login.html?nextUrl=".concat(URLEncoder.encode(
								resourceRef.getPath().replace("register",
										"index.html"), "UTF-8")));
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(url("/register").method(Method.PUT).param("id", id)
						.param("token", admin.getAuthToken()));
				return null;
			}

			School school = new School(schoolId);
			school.setAddress(form.getFirstValue("address"));
			school.setPhoneNo(form.getFirstValue("phonenumber"));
			school.setMobile(form.getFirstValue("mobilenumber"));
			school.setSiteUrl(form.getFirstValue("website"));
			school.setEmail(form.getFirstValue("e-mail"));
			school.setType(SchoolType.valueOf(form.getFirstValue("category")));
			school.setStudentCount(Integer.parseInt(form
					.getFirstValue("studentcount")));
			school.setStaffCount(Integer.parseInt(form
					.getFirstValue("staffcount")));
			String hostDomain = form.getFirstValue("hostdomain");
			school.setDomainName(hostDomain);
			String password = Utils.getRandomPassword();
			String adminMail = form.getFirstValue("admine-mail");
			school.setOtherFields(new ImmutableMap.Builder<String, String>()
					.put("admin-mail", adminMail).put("key", password).build());
			Metadata meta = new Metadata();
			meta.setStatus(org.sagarius.radix.client.model.enumerations.Status.FOR_APPROVAL);
			meta.setTimestamp(new Date());
			school.setMeta(meta);
			ModelUtils.persistEntity(school);

			DomainMapping mapping = new DomainMapping(KeyFactory.createKey(
					DomainMapping.class.getSimpleName(),
					resourceRef.getHostDomain()));
			mapping.setSchoolId(schoolId);
			ModelUtils.persistEntity(mapping);

			String scope = "https://www.google.com/calendar/feeds/ "
					+ "http://www.google.com/calendar/feeds/ "
					+ "https://www.google.com/m8/feeds/ "
					+ "http://www.google.com/m8/feeds/ "
					+ "https://docs.google.com/feeds/ "
					+ "http://docs.google.com/feeds/ "
					+ "https://mail.google.com/mail/feed/atom/ "
					+ "https://spreadsheets.google.com/feeds/ "
					// + "http://picasaweb.google.com/data/ "
					+ "http://spreadsheets.google.com/feeds/ ";
			Reference reference = new Reference(resourceRef.toString(false,
					false));
			reference.addQueryParameter("id", id);
			reference.addQueryParameter("step", "1");
			reference.addQueryParameter("email", adminMail);
			getResponse().redirectTemporary(
					AuthSubUtil.getRequestUrl(reference.toString(), scope,
							false, true));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
	}

	@Override
	protected Representation head(Variant variant) throws ResourceException {
		String id = getRequest().getResourceRef().getQueryAsForm()
				.getFirstValue("id");
		try {
			PMFactory.getManager().getObjectById(School.class,
					KeyFactory.createKey(School.class.getSimpleName(), id));
		} catch (JDOObjectNotFoundException e) {
			getResponse().setStatus(Status.SUCCESS_OK);
			return null;
		}
		getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT);
		return null;
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String id = form.getFirstValue("id");
		String token = form.getFirstValue("token");

		Key schoolId = KeyFactory.createKey(School.class.getSimpleName(), id);
		FolderEntry schoolFolder = DocumentUtils.createSchoolFolder(id, token);
		School school = PMFactory.getManager().getObjectById(School.class,
				schoolId);
		String folderId = Utils.parseId(schoolFolder.getResourceId(), ":");
		school.setFolderId(folderId);
		ModelUtils.persistEntity(school);
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(url("/year").method(Method.POST).param("token", token)
				.param("folderId", folderId).param("name", "2010-11")
				.param("id", id));
		return null;
	}
}
