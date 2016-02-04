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

public class GatewayMocker extends MessageGateway {
	private static final String SMS_REQUEST_STRING = "http://127.0.0.1:8182/sms?";
	private static final String DEFAULT_SENDER_ID = "test";

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
		StringBuffer buffer = new StringBuffer(SMS_REQUEST_STRING);
		buffer.append("username=" + username + "&");
		buffer.append("password=" + password + "&");
		buffer.append("senderid=" + senderId + "&");
		if (priority == Priority.HIGH) {
			buffer.append("priority=2&");
		}
		buffer.append("to=" + phoneNumbers + "&");
		try {
			buffer.append("message=" + URLEncoder.encode(message, "UTF-8"));
			URL requestUrl = new URL(buffer.toString());
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

	@Override
	public String[] getStatus(String[] ids) {
		return null;
	}

}
