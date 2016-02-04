package com.sagarius.goddess.server;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.ext.servlet.ServletUtils;
import org.sagarius.radix.client.model.enumerations.Status;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.server.model.AcademicYear;
import com.sagarius.goddess.server.model.Document;
import com.sagarius.goddess.server.model.DomainMapping;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.model.School;
import com.sagarius.goddess.server.model.neuf.ShortMessage;
import com.sagarius.goddess.server.util.PMFactory;

public class Utils {
	public static javax.jdo.Query docListQuery;

	public static void logBatchStatus(CellFeed resultsFeed) {
		for (CellEntry entry : resultsFeed.getEntries()) {
			String batchId = BatchUtils.getBatchId(entry);
			if (!BatchUtils.isSuccess(entry)) {
				BatchStatus status = BatchUtils.getBatchStatus(entry);
				System.err.println("Failed entry");
				System.err.println("\t" + batchId + " failed ("
						+ status.getReason() + ") " + status.getContent());
			}
		}
	}

	public static final String getSenderId() {
		Key id = getSchoolId();
		String defaultId = "DataBox";
		if (id == null) {
			return defaultId;
		}
		School school = PMFactory.getManager().getObjectById(School.class, id);
		Map<String, String> fields = school.getOtherFields();
		if (fields == null) {
			return defaultId;
		}
		String senderid = fields.get("senderid");
		if (StringUtils.isEmpty(senderid)) {
			return defaultId;
		}
		return senderid;
	}

	public static final String getColumn(int column, int startIndex) {
		if (column <= 26 + startIndex) {
			return new String(
					new char[] { (char) ('A' + column - 1 - startIndex) });
		}
		char firstLetter = (char) ((column - startIndex) / 26 + 'A' - 1);
		char secondLetter = (char) ((column - startIndex) % 26 + 'A' - 1);
		return new String(new char[] { firstLetter, secondLetter });
	}

	public static final String getRandomPassword() {
		Random random = new Random();
		int len = 5;
		char pass[] = new char[len];
		for (int i = 0; i < len; i++) {
			pass[i] = (char) (random.nextInt(10) + '0');
		}
		return new String(pass);
	}

	@SuppressWarnings("unchecked")
	public static void initializeSchool() {
		Request request = Request.getCurrent();
		HttpSession session = ServletUtils.getRequest(request).getSession();
		if (session.getAttribute("school") != null) {
			return;
		}
		Key schoolId;
		String domain = request.getResourceRef().getHostDomain();
		PersistenceManager manager = PMFactory.getManager();
		DomainMapping mapping = manager.getObjectById(DomainMapping.class,
				KeyFactory.createKey(DomainMapping.class.getSimpleName(),
						domain));
		schoolId = mapping.getSchoolId();
		session.setAttribute("school", schoolId);

		Query query = manager.newQuery(AcademicYear.class);
		query.setFilter("isDefault==defaultParam && metadata.schoolId==schoolParam");
		query.declareParameters("java.lang.Boolean defaultParam, com.google.appengine.api.datastore.Key schoolParam");
		Key acaYearId = ((Collection<AcademicYear>) query.execute(true,
				schoolId)).iterator().next().getRefId();
		session.setAttribute("currentYear", acaYearId);

		School school = manager.getObjectById(School.class, schoolId);
		session.setAttribute("gdomain", school.getDomainName());

		query = manager.newQuery(Member.class);
		query.setFilter("metadata.schoolId==schoolParam && type==typeParam");
		query.declareParameters("com.google.appengine.api.datastore.Key schoolParam,"
				+ "com.sagarius.goddess.client.model.enumerations.MemberType typeParam");
		query.setOrdering("refId desc");
		Member member = ((Collection<Member>) query.execute(schoolId,
				MemberType.ADMINISTRATOR)).iterator().next();
		// session.setAttribute("gToken", member.getGtoken());
		String picasaToken = member.getOtherFields().get("picasa");
		session.setAttribute("picToken", picasaToken);
	}

	@SuppressWarnings("unchecked")
	public static final String getRobotToken() {
		PersistenceManager manager = PMFactory.getManager();
		Key schoolId = getSchoolId();
		Query query = manager.newQuery(Member.class);
		query.setFilter("metadata.schoolId==schoolParam && type==typeParam");
		query.declareParameters("com.google.appengine.api.datastore.Key schoolParam,"
				+ "com.sagarius.goddess.client.model.enumerations.MemberType typeParam");
		query.setOrdering("refId desc");
		Member member = ((Collection<Member>) query.execute(schoolId,
				MemberType.ADMINISTRATOR)).iterator().next();
		return member.getAuthToken();
	}

	public static Key getAcademicYear() {
		return (Key) ServletUtils.getRequest(Request.getCurrent()).getSession()
				.getAttribute("currentYear");
	}

	public static Key getSchoolId() {
		return (Key) ServletUtils.getRequest(Request.getCurrent()).getSession()
				.getAttribute("school");
	}

	public static Key getCurrentMember() {
		return (Key) ServletUtils.getRequest(Request.getCurrent()).getSession()
				.getAttribute("member");
	}

	public static void helpBatch(CellEntry cellEntry, CellFeed cellFeed,
			String newValue) {
		cellEntry.changeInputValueLocal(newValue);
		cellFeed.getEntries().add(cellEntry);
		Cell cell = cellEntry.getCell();
		BatchUtils.setBatchId(cellEntry, cell.getRow() + "" + cell.getCol());
		BatchUtils.setBatchOperationType(cellEntry, BatchOperationType.UPDATE);
	}

	@SuppressWarnings("unchecked")
	public static String getSheetKeyByType(DocumentType type) {
		if (docListQuery == null) {
			docListQuery = PMFactory.getManager().newQuery(
					"select documentKey from " + Document.class.getName());
			docListQuery
					.setFilter("type==typeParam && academicYear==yearParam");
			docListQuery
					.declareParameters("com.sagarius.goddess.client.model.enumerations.DocumentType typeParam, "
							+ "com.google.appengine.api.datastore.Key yearParam");
		}
		return ((Collection<Key>) (docListQuery.execute(type,
				ServletUtils.getRequest(Request.getCurrent()).getSession()
						.getAttribute("currentYear")))).iterator().next()
				.getName();
	}

	public static final FeedURLFactory URL_FACTORY = FeedURLFactory
			.getDefault();

	public static String getGoogleToken() {
		return (String) ServletUtils.getRequest(Request.getCurrent())
				.getSession().getAttribute("gToken");
	}

	public static String getPhoneNo() {
		return (String) ServletUtils.getRequest(Request.getCurrent())
				.getSession().getAttribute("phone");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Student> getStudList() {
		return (Map<String, Student>) ServletUtils
				.getRequest(Request.getCurrent()).getSession()
				.getAttribute("students");
	}

	public static Metadata getMetadata() {
		Metadata meta = new Metadata();
		meta.setStatus(Status.ACTIVE);
		meta.setSchoolId(getSchoolId());
		Key user = getCurrentMember();
		meta.setCreator(user);
		meta.setEditor(user);
		Date now = new Date();
		meta.setPublished(now);
		meta.setTimestamp(now);
		return meta;
	}

	public static void saveMessages(String numbers, String status,
			ShortMessage parentMessage) {
		List<ShortMessage> messages = new ArrayList<ShortMessage>();
		String[] nums = numbers.split(",");
		String[] ids = status.split(",");
		Metadata meta = getMetadata();
		Key parentKey = parentMessage.getRefId();
		for (int i = 0; i < ids.length; i++) {
			ShortMessage message = new ShortMessage();
			message.setId(ids[i]);
			message.setPhone(nums[i]);
			message.setMeta(meta);
			message.setParent(parentKey);
			messages.add(message);
		}
		com.sagarius.goddess.server.util.ModelUtils.persistEntities(messages);
	}

	public static String parseId(String fullId, String separator) {
		if (fullId == null) {
			return null;
		}
		String[] parts = fullId.split(separator);
		return parts[parts.length - 1];
	}

	public static AclFeed getAclFeed(String folderId) {
		DocsService service = new DocsService("Goddess");
		service.setAuthSubToken(getGoogleToken());
		try {
			URL folderEntryUrl = new URL(
					"https://docs.google.com/feeds/default/private/expandAcl/folder%3A"
							.concat(folderId));
			DocumentListEntry listEntry = service.getEntry(folderEntryUrl,
					DocumentListEntry.class);
			return listEntry.getAclFeed();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String getColString(int column) {
		return column < 26 ? new String(new char[] { (char) (column + 'A') })
				: new String(new char[] { 'A', (char) (column + 'A' - 26) });
	}

	public static int getCol(String colString) {
		if (colString.length() == 1) {
			return colString.charAt(0) - 'A' + 1;
		}
		return 26 + colString.charAt(1) - 'A' + 1;
	}
}
