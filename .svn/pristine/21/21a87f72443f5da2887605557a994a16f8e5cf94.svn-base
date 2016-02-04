package com.sagarius.goddess.server.resources.mapreduce;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

public class ZGroup extends SubjectGroup {
	public ZGroup() {
		type = SubjectType.ZERO;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public List<String> getSubjects() {
		if (subjects == null) {
			subjects = new LinkedList<String>();
		}
		return subjects;
	}

	private List<String> subjects;

	@SuppressWarnings("unchecked")
	@Override
	public String toJSONString() {
		JSONObject result = new JSONObject();
		result.put("name", name);
		result.put("level", level);
		result.put("type", type == null ? null : type.toString());
		result.put("children", children);
		result.put("startColumn", startColString);
		result.put("endColumn", endColString);
		result.put("startIndex", startColumn);
		result.put("endIndex", endColumn);
		result.put("subjects", subjects);
		return result.toJSONString();

	}
}
