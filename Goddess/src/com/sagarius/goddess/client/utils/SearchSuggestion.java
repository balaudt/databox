package com.sagarius.goddess.client.utils;

import java.io.Serializable;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.sagarius.goddess.client.model.enumerations.MemberType;

public class SearchSuggestion implements Suggestion, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String id;
	private MemberType type;

	public SearchSuggestion(String name, String id, MemberType type) {
		this.name = name;
		this.id = id;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getDisplayString() {
		return name;
	}

	@Override
	public String getReplacementString() {
		return "";
	}

	public MemberType getType() {
		return type;
	}

	public void setType(MemberType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SearchSuggestion [id=" + id + ", name=" + name + ", type="
				+ type + "]";
	}

	public SearchSuggestion() {
	}

}
