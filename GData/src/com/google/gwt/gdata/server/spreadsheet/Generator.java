package com.google.gwt.gdata.server.spreadsheet;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.sagarius.radix.client.collect.Maps;

public class Generator {
	public static void main(String[] args) {
		modelGenerate();
	}

	static void modelGenerate() {
		StrBuilder builder = new StrBuilder();
		Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
				.put("refId", new Object[] { 0, "Reference Id" })
				.put("meta", new Object[] { 1, "Meta data" })
				.put("name", new Object[] { 2, "Name" })
				.put("message", new Object[] { 3, "Message" }).build();
		Set<String> keys = FIELD_LIST.keySet();
		for (String key : keys) {
			builder.append(key)
					.append(" = entry.get((Integer) FIELD_LIST.get(\"")
					.append(key).appendln("\")[0], String.class);");
		}
		System.out.println(builder.toString());

		for (String key : keys) {
			System.out.println(generateSetter(key));
		}
	}

	public static String generateSetter(String field) {
		StrBuilder builder = new StrBuilder();
		builder.append("public void set").append(StringUtils.capitalize(field))
				.append("(String ").append(field).appendln("){");
		builder.append("String old").append(field).append(" = this.")
				.append(field).appendln(";");
		builder.append("this.").append(field).append(" = ").append(field)
				.appendln(";");
		builder.append("support.firePropertyChange(\"").append(field)
				.append("\", old").append(field).append(", ").append(field)
				.appendln(");");
		builder.append("entry.set((Integer) FIELD_LIST.get(\"").append(field)
				.append("\")[0], ").append(field).appendln(");");
		builder.appendln("}");
		return builder.toString();
	}

	public static String generateFeed(String type) {
		StrBuilder builder = new StrBuilder();
		builder.appendln("import com.google.gwt.core.client.JavaScriptObject;")
				.appendln("import com.google.gwt.gdata.client.Feed;");
		builder.appendln("");
		builder.append("public class ").append(type)
				.append("Feed extends Feed<").append(type).append("Feed, ")
				.append(type).appendln("Entry> {");

		// Default Constructor
		builder.append("protected ").append(type).appendln("Feed() {")
				.appendln("}");
		builder.appendln("");
		// Instantiator
		builder.appendln("@SuppressWarnings(\"unchecked\")");
		builder.append("public static native ").append(type)
				.appendln("Feed newInstance()/*-{");
		builder.appendln("return new $wnd.google.gdata.Feed();");
		builder.appendln("}-*/;");
		builder.appendln("");
		// Constructor instance
		builder.appendln("public static final native JavaScriptObject getConstructor()/*-{");
		builder.appendln("return $wnd.google.gdata.Feed;");
		builder.appendln("}-*/;");
		builder.appendln("");

		builder.append("}");
		return builder.toString();
	}

	public static String generateQuery(String type) {
		StrBuilder builder = new StrBuilder();
		builder.append("public class ").append(type)
				.appendln("Query extends GDataQuery{");

		// Default Constructor
		builder.append("protected ").append(type).appendln("Query() {")
				.appendln("}");
		builder.appendln("");
		// Instantiator
		builder.append("public static native ").append(type)
				.appendln("Query newInstance(String feedUri)/*-{");
		builder.appendln("return new $wnd.google.gdata.client.Query(feedUri);");
		builder.appendln("}-*/;");
		builder.appendln("");

		builder.append("}");
		return builder.toString();
	}

	public static String generateEntry(String type) {
		StrBuilder builder = new StrBuilder();
		builder.appendln("import com.google.gwt.core.client.JavaScriptObject;")
				.appendln("import com.google.gwt.gdata.client.Entry;");
		builder.appendln("");
		builder.append("public class ").append(type)
				.append("Entry extends Entry<").append(type)
				.appendln("Entry> {");

		// Default Constructor
		builder.append("protected ").append(type).appendln("Entry() {")
				.appendln("}");
		builder.appendln("");
		// Instantiator
		builder.append("public static native ").append(type)
				.appendln("Entry newInstance()/*-{");
		builder.appendln("return new $wnd.google.gdata.Entry();");
		builder.appendln("}-*/;");
		builder.appendln("");
		// Constructor instance
		builder.appendln("public static final native JavaScriptObject getConstructor()/*-{");
		builder.appendln("return $wnd.google.gdata.Entry;");
		builder.appendln("}-*/;");
		builder.appendln("");

		builder.append("}");
		return builder.toString();
	}

	public static String generateFeedCallback(String type) {
		StrBuilder builder = new StrBuilder();
		builder.appendln("import com.google.gwt.gdata.client.impl.Callback;");
		builder.append("public interface ").append(type)
				.append("FeedCallback extends Callback<").append(type)
				.appendln("Feed> {");
		builder.appendln("");
		builder.append("}");
		return builder.toString();
	}

	public static String generateRandomPhoneNumbers(int count) {
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < 10; j++) {
				buffer.append(random.nextInt(10));
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
