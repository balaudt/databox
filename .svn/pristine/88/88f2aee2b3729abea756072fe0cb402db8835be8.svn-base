package com.sagarius.goddess.server.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;

public abstract class SMSGateway extends MessageGateway {

	protected String smsRequestString = "http://bulksms.gateway4sms.com/pushsms.php?";
	protected String dlrString = "http://bulksms.gateway4sms.com/fetchdlr.php?msgid=";
	private static final String DEFAULT_SENDER_ID = "DataBox";

	@Override
	public String[] getStatus(String[] ids) {
		StrBuilder builder = new StrBuilder();
		for (String id : ids) {
			builder.append(id).append(",");
		}
		try {
			String url = dlrString.concat(builder.toString());
			System.out.println(url);
			URL requestUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) requestUrl
					.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line = null;
			builder = new StrBuilder("");
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			String[] statuses = builder.toString().split(",");
			String[] result = new String[statuses.length];
			int i = 0;
			for (String status : statuses) {
				result[i++] = status.split(":")[1];
			}
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public MessageResponse send(String message, String phoneNumbers,
			String senderId, Priority priority) {
		if (StringUtils.isEmpty(phoneNumbers) || StringUtils.isEmpty(message)) {
			return new MessageResponse();
		}
		String username = System.getProperty("sms.username");
		String password = System.getProperty("sms.password");
		if (senderId == null) {
			senderId = DEFAULT_SENDER_ID;
		}
		StringBuffer buffer = new StringBuffer(smsRequestString);
		buffer.append("username=" + username + "&");
		buffer.append("password=" + password + "&");
		buffer.append("sender=" + senderId + "&");
		if (priority == Priority.HIGH) {
			buffer.append("priority=2&");
		}
		buffer.append("to=" + phoneNumbers + "&");
		try {
			buffer.append("message=" + URLEncoder.encode(message, "UTF-8"));
			URL requestUrl = new URL(buffer.toString());
			System.out.println(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) requestUrl
					.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line = null;
			StringBuilder builder = new StringBuilder("");
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			MessageResponse status = new MessageResponse();
			status.setStatus(builder.toString());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				status.setHasSucceeded(true);
			} else {
				status.setHasSucceeded(false);
			}
			return status;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
