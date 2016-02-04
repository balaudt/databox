package com.sagarius.goddess.server.resources.base;

import java.net.URLEncoder;
import java.util.Collection;

import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.ext.servlet.ServletUtils;

import com.google.gdata.client.http.AuthSubUtil;
import com.sagarius.goddess.client.model.enumerations.MemberType;
import com.sagarius.goddess.server.model.Member;
import com.sagarius.goddess.server.util.ModelUtils;
import com.sagarius.goddess.server.util.PMFactory;

public class AuthSubRestlet extends Restlet {
	String scope = "http://www.google.com/calendar/feeds/ "
			+ "http://www.google.com/m8/feeds/ "
			+ "http://docs.google.com/feeds/ "
			+ "https://mail.google.com/mail/feed/atom/ "
			+ "http://spreadsheets.google.com/feeds/ ";

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Request request, Response response) {
		Form form = request.getResourceRef().getQueryAsForm();
		String token = form.getFirstValue("token");
		if (token == null) {
			return;
		}
		try {
			String sessionToken = AuthSubUtil.exchangeForSessionToken(token,
					null);
			HttpSession session = ServletUtils.getRequest(request).getSession();
			String email = form.getFirstValue("email");
			Member member = new Member();
			member.setEmail(email);
			member.setName(form.getFirstValue("name"));
			member.setAuthToken(sessionToken);
			member.setType(MemberType.STAFF);
			member.setMetadata(com.sagarius.goddess.server.Utils.getMetadata());
			ModelUtils.persistEntity(member);
			response.getCookieSettings().add(
					"g314-scope-0",
					"scope=" + URLEncoder.encode(scope, "UTF-8") + "&token="
							+ URLEncoder.encode(sessionToken, "UTF-8"));
			session.setAttribute("gToken", sessionToken);
			session.setAttribute("member", member.getRefId());

			Query query = PMFactory.getManager().newQuery(Member.class);
			query.setFilter("metadata.schoolId==schoolParam && type==typeParam");
			query.declareParameters("com.google.appengine.api.datastore.Key schoolParam,"
					+ " com.sagarius.goddess.client.model.enumerations.MemberType typeParam");
			Collection<Member> members = (Collection<Member>) query.execute(
					com.sagarius.goddess.server.Utils.getSchoolId(),
					MemberType.ADMINISTRATOR);
			String picToken = members.iterator().next().getOtherFields()
					.get("picasa");
			session.setAttribute("picToken", picToken);

			String nextUrl = (String) session.getAttribute("nextUrl");
			response.redirectTemporary(nextUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
