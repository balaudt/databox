package com.sagarius.goddess.server.resources.base;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.expressme.openid.Association;
import org.expressme.openid.Authentication;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.sagarius.radix.server.model.util.BaseEntry;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.sagarius.goddess.client.model.dto.Student;
import com.sagarius.goddess.client.model.enumerations.DocumentType;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.server.Utils;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.model.Users;
import com.sagarius.goddess.server.util.PMFactory;

public class LoginResource extends ServerResource {
	private static final long ONE_HOUR = 3600000L;

	public LoginResource() {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		ServletUtils.getRequest(getRequest()).getSession().invalidate();
		return new EmptyRepresentation();
	}

	@Override
	protected Representation head(Variant variant) throws ResourceException {
		Utils.initializeSchool();
		HttpSession session = ServletUtils.getRequest(getRequest())
				.getSession();
		String gToken = (String) session.getAttribute("gToken");
		Response response = getResponse();
		if (gToken == null) {
			response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return null;
		}
		response.setStatus(Status.SUCCESS_OK);
		Form responseHeaders = (Form) response.getAttributes().get(
				"org.restlet.http.headers");
		if (responseHeaders == null) {
			responseHeaders = new Form();
			response.getAttributes().put("org.restlet.http.headers",
					responseHeaders);
		}
		String phone = (String) session.getAttribute("phone");
		String result;
		if (phone != null) {
			result = phone + "," + MemberType.PARENT;
		} else {
			PersistenceManager manager = PMFactory.getManager();
			Member member = manager.getObjectById(Member.class,
					session.getAttribute("member"));
			result = member.getName() + "," + MemberType.STAFF;
		}
		Object domain = session.getAttribute("domain");
		if (domain != null) {
			result += "," + domain;
		}
		responseHeaders.add("Login-Data", result);
		return new EmptyRepresentation();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		Request request = getRequest();
		Reference resourceRef = request.getResourceRef();
		String hostDomain = resourceRef.getHostDomain();
		HttpServletRequest httpRequest = ServletUtils.getRequest(request);
		HttpSession session = httpRequest.getSession();
		Form form = resourceRef.getQueryAsForm();
		String nextUrl = form.getFirstValue("nextUrl");
		if (nextUrl != null) {
			session.setAttribute("nextUrl", nextUrl);
		}
		Key schoolId = (Key) session.getAttribute("school");
		if (schoolId == null) {
			Utils.initializeSchool();
		} else {
			String gtoken = (String) session.getAttribute("gToken");
			if (gtoken != null) {
				getResponse().redirectTemporary(nextUrl);
				return null;
			}
		}

		String isLogin = form.getFirstValue("op");
		String domain = form.getFirstValue("domain");
		if (!StringUtils.isEmpty(domain)) {
			session.setAttribute("domain", domain);
		}
		String endpointStr = null;
		OpenIdManager manager = new OpenIdManager();
		manager.setRealm("http://" + hostDomain);
		manager.setReturnTo("http://" + hostDomain + "/login");
		if (isLogin != null && isLogin.equals("true")) {
			if (domain != null) {
				endpointStr = "https://www.google.com/accounts/o8/site-xrds?hd="
						+ domain;
			} else {
				endpointStr = "https://www.google.com/accounts/o8/id";
			}
			Endpoint endpoint = manager.lookupEndpoint(endpointStr);
			Association association = manager.lookupAssociation(endpoint);
			session.setAttribute("openid_mac", association.getRawMacKey());
			session.setAttribute("openid_alias", endpoint.getAlias());
			getResponse().redirectTemporary(
					manager.getAuthenticationUrl(endpoint, association));
			return null;
		}

		checkNonce(form.getFirstValue("openid.response_nonce"));
		byte[] mac_key = (byte[]) session.getAttribute("openid_mac");
		String alias = (String) session.getAttribute("openid_alias");
		Authentication authentication = manager.getAuthentication(httpRequest,
				mac_key, alias);
		String email = authentication.getEmail();
		String name = authentication.getFirstname();
		session.setAttribute("email", email);
		PersistenceManager jdoManager = PMFactory.getManager();
		Query query = jdoManager.newQuery(Member.class);
		query.setFilter("email==emailParam && metadata.schoolId==schoolParam");
		query.declareParameters("String emailParam,com.google.appengine.api.datastore.Key schoolParam");
		Collection<Member> members = (Collection<Member>) query.execute(email,
				Utils.getSchoolId());
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
		if (members == null || members.isEmpty()) {
			StringBuffer buffer = new StringBuffer(
					resourceRef.getHostIdentifier());
			try {
				buffer.append("/authsub?email=" + email + "&name="
						+ URLEncoder.encode(name, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			getResponse().redirectTemporary(
					AuthSubUtil.getRequestUrl(buffer.toString(), scope, false,
							true));
		} else {
			Member member = members.iterator().next();
			String gtoken = member.getAuthToken();
			query.setFilter("metadata.schoolId==schoolParam && type==typeParam");
			query.declareParameters("com.google.appengine.api.datastore.Key schoolParam,"
					+ " com.sagarius.goddess.client.model.enumerations.MemberType typeParam");
			members = (Collection<Member>) query.execute(Utils.getSchoolId(),
					MemberType.ADMINISTRATOR);
			String picToken = members.iterator().next().getOtherFields()
					.get("picasa");
			session.setAttribute("picToken", picToken);
			session.setAttribute("member", member.getRefId());
			try {
				getResponse().getCookieSettings().add(
						"g314-scope-0",
						"scope=" + URLEncoder.encode(scope, "UTF-8")
								+ "&token="
								+ URLEncoder.encode(gtoken, "UTF-8"));
				session.setAttribute("gToken", gtoken);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			session.setAttribute("gToken", gtoken);
			String nextURL = (String) session.getAttribute("nextUrl");
			getResponse().redirectTemporary(nextURL);
		}
		return null;
	}

	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		String phoneNo = null;
		String password = null;
		try {
			BaseEntry baseEntry = BaseEntry.instantiate(entity.getText());
			phoneNo = (String) baseEntry.get(0);
			password = (String) baseEntry.get(1);
		} catch (IOException e1) {
			e1.printStackTrace();
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		}

		try {
			Users users = PMFactory.getManager().getObjectById(Users.class,
					KeyFactory.createKey(Users.class.getSimpleName(), phoneNo));
			if (!password.equals(users.getPassword())) {
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return null;
			}
			Utils.initializeSchool();
			HttpSession session = ServletUtils.getRequest(getRequest())
					.getSession();
			String robotToken = Utils.getRobotToken();
			session.setAttribute("gToken", robotToken);
			session.setAttribute("phone", phoneNo);
			SpreadsheetService service = new SpreadsheetService("Goddess");
			service.setAuthSubToken(robotToken);
			URL studWorkUrl = FeedURLFactory.getDefault().getWorksheetFeedUrl(
					Utils.getSheetKeyByType(DocumentType.SINGLE_STUDENT),
					"private", "values");
			URL studListUrl = service.getFeed(studWorkUrl, WorksheetFeed.class)
					.getEntries().get(0).getListFeedUrl();
			ListQuery studListQuery = new ListQuery(studListUrl);
			studListQuery.setSpreadsheetQuery("parentsmobile=\"" + phoneNo
					+ "\"");
			List<ListEntry> studEntries = service.getFeed(studListQuery,
					ListFeed.class).getEntries();
			Map<String, Student> studentMap = new HashMap<String, Student>();
			for (ListEntry studEntry : studEntries) {
				CustomElementCollection elements = studEntry
						.getCustomElements();
				studentMap.put(
						elements.getValue("studentid"),
						new Student(elements.getValue("standard") + "-"
								+ elements.getValue("section"), elements
								.getValue("name"), elements
								.getValue("studentid")));
			}
			session.setAttribute("students", studentMap);
			return new EmptyRepresentation();
		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			return null;
		}
	}

	private void checkNonce(String nonce) {
		// check response_nonce to prevent replay-attack:
		if (nonce == null || nonce.length() < 20)
			throw new OpenIdException("Verify failed.");
		long nonceTime = getNonceTime(nonce);
		long diff = System.currentTimeMillis() - nonceTime;
		if (diff < 0)
			diff = (-diff);
		if (diff > ONE_HOUR)
			throw new OpenIdException("Bad nonce time.");
		// TODO: nonce manipulations as given in Jopenid sample program
	}

	private long getNonceTime(String nonce) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(
					nonce.substring(0, 19) + "+0000").getTime();
		} catch (ParseException e) {
			throw new OpenIdException("Bad nonce time.");
		}
	}
}
