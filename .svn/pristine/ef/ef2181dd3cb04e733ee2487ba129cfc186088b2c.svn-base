package com.sagarius.goddess.server.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Users implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent
	@PrimaryKey
	private Key refId;
	@Persistent
	private String password;
	@Persistent
	private Date lastLog;
	@Persistent
	private Integer noOfFail;
	@Persistent(serialized = "true")
	private Map<String, String> otherFields;
	@Persistent
	@Embedded
	private Metadata metadata;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastLog() {
		return lastLog;
	}

	public void setLastLog(Date lastLog) {
		this.lastLog = lastLog;
	}

	public Integer getNoOfFail() {
		return noOfFail;
	}

	public void setNoOfFail(Integer noOfFail) {
		this.noOfFail = noOfFail;
	}

	public Map<String, String> getOtherFields() {
		return otherFields;
	}

	public void setOtherFields(Map<String, String> otherFields) {
		this.otherFields = otherFields;
	}

	@Override
	public String toString() {
		return "Users [refId=" + refId + ", password=" + password
				+ ", lastLog=" + lastLog + ", noOfFail=" + noOfFail
				+ ", otherFields=" + otherFields + ", metadata=" + metadata
				+ "]";
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
		Users other = (Users) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	public Users(Key refId) {
		super();
		this.refId = refId;
	}

	public Users() {
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() {
		return metadata;
	}
}
