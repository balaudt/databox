package org.sagarius.radix.client.uri;

import java.util.HashMap;
import java.util.Map;

public class Template {
	private String uriTemplate;
	private Map<Integer, String> attributes;
	private Map<Integer, String> paths;
	private int length;
	private String regex;

	public Template(String uriTemplate) {
		this.uriTemplate = uriTemplate;
		attributes = new HashMap<Integer, String>();
		paths = new HashMap<Integer, String>();
		String[] templatePaths = uriTemplate.split("/");
		StringBuffer regexBuffer = new StringBuffer();
		for (int i = 0; i < templatePaths.length; i++) {
			if (templatePaths[i].startsWith("{")) {
				attributes.put(i, templatePaths[i].substring(1,
						templatePaths[i].length() - 1));
			} else {
				paths.put(i, templatePaths[i]);
				regexBuffer.append(templatePaths[i] + "/");
			}
		}
		length = templatePaths.length;
		regex = getRegexString();
	}

	private String getRegexString() {
		StringBuffer patternBuffer = new StringBuffer();
		boolean inVariable = false;
		char next;
		int uriTemplateLength = uriTemplate.length();
		for (int i = 0; i < uriTemplateLength; i++) {
			next = uriTemplate.charAt(i);
			if (inVariable) {
				if (next == '}') {
					patternBuffer.append(".*");
					inVariable = false;
				}
			} else {
				if (next == '{') {
					inVariable = true;
				} else {
					patternBuffer.append(quote(next));
				}
			}
		}
		return patternBuffer.toString();
	}

	public boolean isAttributesEmpty() {
		return attributes.isEmpty();
	}

	public boolean isPathsEmpty() {
		return paths.isEmpty();
	}

	public String getAttributeAt(int index) {
		return attributes.get(index);
	}

	public String getPathAt(int index) {
		return paths.get(index);
	}

	public String getUri() {
		return uriTemplate;
	}

	public int length() {
		return length;
	}

	@Override
	public String toString() {
		return "Template [attributes=" + attributes + ", length=" + length
				+ ", paths=" + paths + ", regex=" + regex + ", uriTemplate="
				+ uriTemplate + "]";
	}

	public boolean matches(String uri) {
		return uri.matches(regex);
	}

	private String quote(char character) {
		switch (character) {
		case '[':
			return "\\[";
		case ']':
			return "\\]";
		case '.':
			return "\\.";
		case '\\':
			return "\\\\";
		case '$':
			return "\\$";
		case '^':
			return "\\^";
		case '?':
			return "\\?";
		case '*':
			return "\\*";
		case '|':
			return "\\|";
		case '(':
			return "\\(";
		case ')':
			return "\\)";
		case ':':
			return "\\:";
		case '-':
			return "\\-";
		case '!':
			return "\\!";
		case '<':
			return "\\<";
		case '>':
			return "\\>";
		default:
			return Character.toString(character);
		}
	}

	public Map<Integer, String> getAttributes() {
		return attributes;
	}

	public Map<Integer, String> getPaths() {
		return paths;
	}

}
