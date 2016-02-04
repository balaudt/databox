package com.sagarius.goddess.server.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DomainMapping {
	@Persistent
	@PrimaryKey
	private Key domainName;
	@Persistent
	private Key schoolId;

	public Key getDomainName() {
		return domainName;
	}

	public void setDomainName(Key domainName) {
		this.domainName = domainName;
	}

	public Key getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Key schoolId) {
		this.schoolId = schoolId;
	}

	public DomainMapping(Key domainName) {
		super();
		this.domainName = domainName;
	}

	public DomainMapping() {
		super();
	}

	@Override
	public String toString() {
		return "DomainMapping [domainName=" + domainName + ", schoolId="
				+ schoolId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
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
		DomainMapping other = (DomainMapping) obj;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		return true;
	}

}
