package com.sagarius.goddess.server.resources;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.restlet.data.Form;
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
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.message.MessageGateway;
import com.sagarius.goddess.server.message.MessageGatewayFactory;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.model.Users;
import com.sagarius.goddess.server.util.PMFactory;

public class ParentResource extends ServerResource {
	public ParentResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		Utils.initializeSchool();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String phoneNo = form.getFirstValue("phone");
		SpreadsheetService service = new SpreadsheetService("Goddess");
		service.setAuthSubToken(Utils.getRobotToken());
		try {
			PersistenceManager manager = PMFactory.getManager();
			Query query = manager.newQuery(Member.class);
			query.setFilter("phoneNo==phoneNoParam && metadata.schoolId==schoolParam");
			query.declareParameters("String phoneNoParam,com.google.appengine.api.datastore.Key schoolParam");
			Key schoolId = Utils.getSchoolId();
			Collection<Member> members = (Collection<Member>) query.execute(
					phoneNo, schoolId);
			if (members != null && !members.isEmpty()) {
				getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT);
				return null;
			}
			String studentKey = Utils
					.getSheetKeyByType(DocumentType.SINGLE_STUDENT);
			FeedURLFactory factory = FeedURLFactory.getDefault();
			URL worksheetFeedUrl = factory.getWorksheetFeedUrl(studentKey,
					"private", "values");
			URL listFeedUrl = service
					.getFeed(worksheetFeedUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			ListQuery listQuery = new ListQuery(listFeedUrl);
			listQuery.setSpreadsheetQuery("parentsmobile=" + phoneNo);
			ListFeed outputFeed = service.getFeed(listQuery, ListFeed.class);
			List<ListEntry> results = outputFeed.getEntries();
			if (results.isEmpty()) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}
			String password = Utils.getRandomPassword();
			MessageGateway gateway = MessageGatewayFactory.getGateway();
			String senderId = Utils.getSenderId();
			boolean isSent = gateway.send("Your new password:" + password,
					phoneNo, senderId).isHasSucceeded();
			if (!isSent) {
				getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
				return null;
			}
			Users users = new Users(KeyFactory.createKey(
					Users.class.getSimpleName(), phoneNo));
			users.setNoOfFail(0);
			users.setPassword(com.sagarius.goddess.shared.Utils
					.encryptAsString(password));
			users.setMetadata(Utils.getMetadata());
			com.sagarius.goddess.server.util.ModelUtils.persistEntity(users);
			getResponse().setStatus(Status.SUCCESS_CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return new StringRepresentation(e.getMessage());
		}
		return new EmptyRepresentation();
	}

	@Override
	protected Representation put(Representation representation, Variant variant)
			throws ResourceException {
		Utils.initializeSchool();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		String phoneNo = form.getFirstValue("phone");
		Users users = null;
		try {
			users = PMFactory.getManager().getObjectById(Users.class,
					KeyFactory.createKey(Users.class.getSimpleName(), phoneNo));
		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}
		String pass = form.getFirstValue("pass");
		if (pass == null) {
			// Forgot password?
			String newPassword = Utils.getRandomPassword();
			MessageGateway gateway = MessageGatewayFactory.getGateway();
			String senderId = Utils.getSenderId();
			boolean isSent = gateway.send("Reset password to: " + newPassword,
					phoneNo, senderId).isHasSucceeded();
			if (!isSent) {
				getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
				return null;
			}
			users.setPassword(com.sagarius.goddess.shared.Utils
					.encryptAsString(newPassword));
			com.sagarius.goddess.server.util.ModelUtils.persistEntity(users);
			getResponse().setStatus(Status.SUCCESS_ACCEPTED);
		} else {
			// Password change
		}
		return new EmptyRepresentation();
	}
}
