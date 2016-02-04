package com.sagarius.goddess.server.model;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;
import com.sagarius.goddess.client.model.enumerations.MemberType;

@PersistenceCapable
public class Member implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key refId;
	@Persistent
	private String email;
	@Persistent
	private String phoneNo;
	@Persistent
	private String authToken;
	@Persistent
	private MemberType type;
	@Persistent(serialized = "true")
	private Map<String, String> otherFields;
	@Persistent
	@Embedded
	private Metadata metadata;
	@Persistent
	private String name;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public MemberType getType() {
		return type;
	}

	public void setType(MemberType type) {
		this.type = type;
	}

	public Member() {
		super();
	}

	public Member(Key refId) {
		super();
		this.refId = refId;
	}

	@Override
	public String toString() {
		return "Member [refId=" + refId + ", email=" + email + ", phoneNo="
				+ phoneNo + ", authToken=" + authToken + ", type=" + type
				+ ", otherFields=" + otherFields + ", metadata=" + metadata
				+ ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refId == null) ? 0 : refId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Member other = (Member) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public Map<String, String> getOtherFields() {
		return otherFields;
	}

	public void setOtherFields(Map<String, String> otherFields) {
		this.otherFields = otherFields;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
