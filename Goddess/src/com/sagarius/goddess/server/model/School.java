package com.sagarius.goddess.server.model;

import java.util.Map;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;
import com.sagarius.goddess.client.model.enumerations.SchoolType;

@PersistenceCapable
public class School extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Persistent
	@PrimaryKey
	private Key refId;
	@Persistent
	private String name;
	@Persistent
	private String address;
	@Persistent
	private String email;
	@Persistent
	private Key principal;
	@Persistent
	private String phoneNo;
	@Persistent
	private String mobile;
	@Persistent
	private String siteUrl;
	@Persistent
	private SchoolType type;
	@Persistent
	private String domainName;
	@Persistent
	private String folderId;
	@Persistent
	@Embedded
	private Metadata metadata;
	@Persistent
	private Integer staffCount;
	@Persistent
	private Integer studentCount;

	@Override
	public String toString() {
		return "School [refId=" + refId + ", name=" + name + ", address="
				+ address + ", email=" + email + ", principal=" + principal
				+ ", phoneNo=" + phoneNo + ", mobile=" + mobile + ", siteUrl="
				+ siteUrl + ", type=" + type + ", domainName=" + domainName
				+ ", folderId=" + folderId + ", metadata=" + metadata
				+ ", staffCount=" + staffCount + ", studentCount="
				+ studentCount + ", otherFields=" + otherFields + "]";
	}

	@Persistent(serialized = "true")
	private Map<String, String> otherFields;

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Key getPrincipal() {
		return principal;
	}

	public void setPrincipal(Key principal) {
		this.principal = principal;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public Map<String, String> getOtherFields() {
		return otherFields;
	}

	public void setOtherFields(Map<String, String> otherFields) {
		this.otherFields = otherFields;
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
		School other = (School) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	public School() {
		super(com.sagarius.goddess.client.model.School.FIELD_LIST);
	}

	public School(Key refId) {
		super(com.sagarius.goddess.client.model.School.FIELD_LIST);
		this.refId = refId;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public String render() {
		return render(School.class);
	}

	@Override
	public Metadata getMeta() {
		return metadata;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setType(SchoolType type) {
		this.type = type;
	}

	public SchoolType getType() {
		return type;
	}

	public void setStaffCount(Integer staffCount) {
		this.staffCount = staffCount;
	}

	public Integer getStaffCount() {
		return staffCount;
	}

	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
	}

	public Integer getStudentCount() {
		return studentCount;
	}

	@Override
	public void setMeta(Metadata metadata) {
		this.metadata = metadata;
	}
}
