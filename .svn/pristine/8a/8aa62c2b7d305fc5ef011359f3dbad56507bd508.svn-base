package com.sagarius.goddess.server.resources.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.client.model.enumerations.SchoolType;
import com.sagarius.goddess.server.model.AcademicYear;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.model.DomainMapping;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class UtilityRestlet extends Restlet {
	@Override
	public void handle(Request request, Response response) {
		Member member = PMFactory.getManager().getObjectById(Member.class,
				KeyFactory.createKey(Member.class.getSimpleName(), 798));
		System.out.println(member.getAuthToken() + "\t"
				+ member.getOtherFields().get("picasa"));
	}

	public void setSenderIdOnly(String school, String senderId) {
		try {
			DatastoreService service = DatastoreServiceFactory
					.getDatastoreService();
			Entity entity = service.get(KeyFactory.createKey("School", school));
			ByteArrayOutputStream bostream = new ByteArrayOutputStream();
			ObjectOutputStream ostream = new ObjectOutputStream(bostream);
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("senderid", senderId);
			ostream.writeObject(map);
			entity.setProperty("otherFields", new Blob(bostream.toByteArray()));
			persistEntity(service, entity);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void folderMigration() {
		try {
			DatastoreService service = DatastoreServiceFactory
					.getDatastoreService();
			Entity entity = service.get(KeyFactory.createKey("School", "demo"));
			Builder<String, Object> builder = new ImmutableMap.Builder<String, Object>();
			builder.put("phoneNo", "0422-xxxxxx");
			builder.put("mobile", "90000000000");
			builder.put("siteUrl", "www.databox.in");
			builder.put("type", SchoolType.MATRICULATION.toString());
			builder.put("folderId",
					"0B1KVPWzSYPMCYzJkMmQxMzItOTkxYi00YjVjLWFkNWYtZTE0ZjY5ZDUwNjMz");
			builder.put("staffCount", 80);
			builder.put("studentCount", 3000);
			ImmutableSet<Entry<String, Object>> entrySet = builder.build()
					.entrySet();
			for (Entry<String, Object> entry : entrySet) {
				entity.setProperty(entry.getKey(), entry.getValue());
			}
			persistEntity(service, entity);

			entity = service.get(KeyFactory.createKey("School", "cms"));
			builder = new ImmutableMap.Builder<String, Object>();
			builder.put("phoneNo", "0422-xxxxxx");
			builder.put("mobile", "90000000000");
			builder.put("siteUrl", "www.cmsmhss.com");
			builder.put("type", SchoolType.MATRICULATION.toString());
			builder.put("folderId",
					"0BzBzF1b4bxoANWRhOWZmYjQtMTNlZi00Nzc2LWJkNmQtZDk3Zjk4MGIyMzBi");
			builder.put("staffCount", 80);
			builder.put("studentCount", 3000);
			entrySet = builder.build().entrySet();
			for (Entry<String, Object> entry : entrySet) {
				entity.setProperty(entry.getKey(), entry.getValue());
			}
			persistEntity(service, entity);

			entity = service.get(KeyFactory.createKey("School", "srmhsscbe"));
			builder = new ImmutableMap.Builder<String, Object>();
			builder.put("phoneNo", "0422-xxxxxx");
			builder.put("mobile", "90000000000");
			builder.put("siteUrl", "www.srmhsscbe.databox.in");
			builder.put("type", SchoolType.MATRICULATION.toString());
			builder.put("folderId",
					"0BzEx7Ijj5q9CMzhhZDMyYmYtYjZiYS00MjI4LTg3MzctMWJlNDk2ZmY5MDMz");
			builder.put("staffCount", 80);
			builder.put("studentCount", 3000);
			entrySet = builder.build().entrySet();
			for (Entry<String, Object> entry : entrySet) {
				entity.setProperty(entry.getKey(), entry.getValue());
			}
			persistEntity(service, entity);

			entity = service.get(KeyFactory.createKey("AcademicYear", 1));
			entity.setProperty("folderId",
					"0B-PgApigQS6SMTg1ODQ3MzMtZGZiOC00ZTc0LTljNjItMmM5ODczNDhkYTg0");
			persistEntity(service, entity);

			entity = service.get(KeyFactory.createKey("AcademicYear", 3001));
			entity.setProperty("folderId",
					"0BzQWPpUBMYezYzY2NjQ1ZjktNjNlOC00NWE1LTg2NDMtMWU2ZjBlZjcxMDEz");
			persistEntity(service, entity);
			entity = service.get(KeyFactory.createKey("AcademicYear", 38001));
			entity.setProperty("folderId",
					"0BzQWPpUBMYezYjI1ODI3NTUtMzRlMC00NDE5LTk2NmYtNGQ5ZjVhNzhkYzIw");
			persistEntity(service, entity);

			entity = service.get(KeyFactory.createKey("AcademicYear", 85006));
			entity.setProperty("folderId",
					"0BzEx7Ijj5q9CMTY0NmM4MWUtZjBiZS00YWY5LTk3ZjMtYTc1MmRjODA1ODQz");
			persistEntity(service, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void persistEntity(DatastoreService service, Entity entity) {
		Transaction transaction = null;
		try {
			transaction = service.beginTransaction();
			service.put(entity);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (transaction.isActive()) {
				transaction.rollback();
			}
		}
	}

	public void setSenderIds() {
		PersistenceManager manager = PMFactory.getManager();
		School school = manager.getObjectById(School.class,
				KeyFactory.createKey("School", "demo"));
		Map<String, String> fields = school.getOtherFields();
		if (fields == null) {
			fields = new HashMap<String, String>();
		}
		fields.put("senderid", "DataBox");
		school.setOtherFields(fields);
		ModelUtils.persistEntity(school);
		school = manager.getObjectById(School.class,
				KeyFactory.createKey("School", "srmhsscbe"));
		fields = school.getOtherFields();
		if (fields == null) {
			fields = new HashMap<String, String>();
		}
		fields.put("senderid", "SRMHSS");
		school.setOtherFields(fields);
		ModelUtils.persistEntity(school);
	}

	public void srm() {
		String schoolId = "srmhsscbe";
		createSchool(
				schoolId,
				"SNR College Road, Rathinasabapathy Puram, Coimbatore - 641006",
				"admin@srmhsscbe.databox.in",
				"Sri Ramakrishna Matriculation Higher Secondary School",
				"0422-2562799", "srmhsscbe.databox.in", null);
		createMember(
				"admin@srmhsscbe.databox.in",
				"0422-25262799",
				MemberType.ADMINISTRATOR,
				"1/WBwz_NHhgtKpwWPawkA5v4-vSzLvKFmPYyeG8A-45WU",
				"srmhsscbe",
				new ImmutableMap.Builder<String, String>().put("picasa",
						"1/oBXigV_ACLAuvmUhWoVJr5CIKRZx3uhhszMonne0Fgo")
						.build());
		Key yearKey = createAcademicYear(schoolId, "2010-2011", "2010-11", true);
		createDocument(yearKey, "t_FjIcEfuCi4yZlPEi-TkqA",
				DocumentType.SINGLE_STUDENT, "Student list",
				"student-list-2010-11",
				"0AjqfkJD1ZuR0dF9GakljRWZ1Q2k0eVpsUEVpLVRrcUE", null, null,
				null);
		createDocument(yearKey, "tkI78BmjwP4hl3wfH7ZciAA", DocumentType.STAFF,
				"staff-list-2010-11", "Staff list",
				"0AjEx7Ijj5q9CdGtJNzhCbWp3UDRobDN3Zkg3WmNpQUE", null, null,
				null);
		createDocument(yearKey, "t826dJNna4AUVqAZoXlXmNA", DocumentType.CLASS,
				"class-list-2010-11", "Class list",
				"0AjEx7Ijj5q9CdDgyNmRKTm5hNEFVVnFBWm9YbFhtTkE", null, null,
				null);
		createDocument(yearKey, "tvY-2Oi3uoOYuR1A6sn7Ntg", DocumentType.CSSREL,
				"relations-list-2010-11", "Relations list",
				"0AjEx7Ijj5q9CdHZZLTJPaTN1b09ZdVIxQTZzbjdOdGc", null, null,
				null);
		createDocument(yearKey, "t9czLOeW002vC1emF61WCSQ", DocumentType.DUMMY,
				"app-data-2010-11", "App Data",
				"0AjEx7Ijj5q9CdDljekxPZVcwMDJ2QzFlbUY2MVdDU1E", null, null,
				null);
	}

	public void changeSchool(String school) {
		DomainMapping mapping = PMFactory.getManager().getObjectById(
				DomainMapping.class,
				KeyFactory.createKey(DomainMapping.class.getSimpleName(),
						"127.0.0.1"));
		mapping.setSchoolId(KeyFactory.createKey("School", school));
		ModelUtils.persistEntity(mapping);
	}

	public void createMapping(String domainName, String schoolId) {
		DomainMapping mapping = new DomainMapping();
		mapping.setDomainName(KeyFactory.createKey(
				DomainMapping.class.getSimpleName(), domainName));
		mapping.setSchoolId(KeyFactory.createKey(School.class.getSimpleName(),
				schoolId));
		ModelUtils.persistEntity(mapping);
	}

	public void createDocument(Key academicYear, String documentKey,
			DocumentType type, String title, String name, String cccKey,
			String param1, String param2, String param3) {
		Document document = new Document();
		document.setDocumentKey(KeyFactory.createKey("Document", documentKey));
		document.setType(type);
		document.setAcademicYear(academicYear);
		document.setDocTitle(title);
		document.setDocName(name);
		document.setCccKey(cccKey);
		document.setParam1(param1);
		document.setParam1(param2);
		document.setParam1(param3);
		ModelUtils.persistEntity(document);
	}

	public Key createAcademicYear(String schoolId, String displayString,
			String docSuffix, boolean isDefault) {
		AcademicYear year = new AcademicYear();
		year.setMetadata(com.sagarius.goddess.server.Utils.getMetadata());
		year.getMetadata()
				.setSchoolId(KeyFactory.createKey("School", schoolId));
		year.setDisplayString(displayString);
		year.setDocSuffix(docSuffix);
		year.setDefault(isDefault);
		year = ModelUtils.persistEntity(year);
		return year.getRefId();
	}

	public void createMember(String email, String phoneNo, MemberType type,
			String authToken, String schoolId, Map<String, String> otherFields) {
		Member member = new Member();
		member.setEmail(email);
		member.setPhoneNo(phoneNo);
		member.setType(type);
		member.setAuthToken(authToken);
		member.setMetadata(com.sagarius.goddess.server.Utils.getMetadata());
		member.getMetadata().setSchoolId(
				KeyFactory.createKey(School.class.getSimpleName(), schoolId));
		member.setOtherFields(otherFields);
		ModelUtils.persistEntity(member);
	}

	public void createSchool(String refId, String address, String email,
			String name, String phoneNo, String domain,
			Map<String, String> otherFields) {
		School school = new School();
		school.setRefId(KeyFactory.createKey("School", refId));
		school.setAddress(address);
		school.setEmail(email);
		school.setName(name);
		school.setPhoneNo(phoneNo);
		school.setDomainName(domain);
		school.setOtherFields(otherFields);
		ModelUtils.persistEntity(school);
	}

	public void changeSchool() {
		DomainMapping mapping = PMFactory.getManager().getObjectById(
				DomainMapping.class,
				KeyFactory.createKey(DomainMapping.class.getSimpleName(),
						"127.0.0.1"));
		mapping.setSchoolId(KeyFactory.createKey("School", "demo"));
		ModelUtils.persistEntity(mapping);
	}
}
