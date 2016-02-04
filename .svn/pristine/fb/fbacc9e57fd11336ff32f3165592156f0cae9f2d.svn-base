package com.sagarius.goddess.client.model;

import java.util.Map;

import org.sagarius.radix.client.collect.Maps;
import org.sagarius.radix.client.model.BaseEntity;
import org.sagarius.radix.client.model.Metadata;
import org.sagarius.radix.client.model.util.BaseEntry;

/**
 * @author bala
 * 
 */
public class School extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public School() {
		super(BaseEntry.create());
	}

	public School(String json) {
		super(BaseEntry.create(json));
		init();
	}

	public void init() {
		phoneNo = entry.get((Integer) FIELD_LIST.get("phoneNo")[0],
				String.class);
		principal = entry.get((Integer) FIELD_LIST.get("principal")[0],
				String.class);
		email = entry.get((Integer) FIELD_LIST.get("email")[0], String.class);
		address = entry.get((Integer) FIELD_LIST.get("address")[0],
				String.class);
		name = entry.get((Integer) FIELD_LIST.get("name")[0], String.class);
		refId = entry.get((Integer) FIELD_LIST.get("refId")[0], String.class);
		mobile = entry.get((Integer) FIELD_LIST.get("mobile")[0], String.class);
		siteUrl = entry.get((Integer) FIELD_LIST.get("siteUrl")[0],
				String.class);
		type = entry.get((Integer) FIELD_LIST.get("type")[0], String.class);
		staffCount = entry.get((Integer) FIELD_LIST.get("staffCount")[0],
				Integer.class);
		studentCount = entry.get((Integer) FIELD_LIST.get("studentCount")[0],
				Integer.class);
		domainName = entry.get((Integer) FIELD_LIST.get("domainName")[0],
				String.class);
		BaseEntry baseEntry = entry.get(
				(Integer) FIELD_LIST.get("metadata")[0], BaseEntry.class);
		metadata = new Metadata(baseEntry);
	}

	public static final Map<String, Object[]> FIELD_LIST = new Maps.ImmutableHashMap<String, Object[]>()
			.put("refId", new Object[] { 0, "Reference Id" })
			.put("name", new Object[] { 2, "Name" })
			.put("address", new Object[] { 3, "Address" })
			.put("email", new Object[] { 4, "E-mail" })
			.put("principal", new Object[] { 5, "Principal" })
			.put("phoneNo", new Object[] { 6, "Phone number" })
			.put("domainName", new Object[] { 7, "Domain name" })
			.put("folderId", new Object[] { 8, "Folder" })
			.put("metadata", new Object[] { 9, "Meta data" })
			.put("mobile", new Object[] { 10, "Mobile" })
			.put("siteUrl", new Object[] { 11, "Site url" })
			.put("staffCount", new Object[] { 12, "Staff count" })
			.put("studentCount", new Object[] { 13, "Student count" })
			.put("type", new Object[] { 14, "Type" }).build();
	private String refId;
	private String name;
	private String address;
	private String email;
	private String principal;
	private String phoneNo;
	private String domainName;
	private String folderId;
	private String mobile;
	private String siteUrl;
	private Metadata metadata;
	private Integer staffCount;
	private Integer studentCount;
	private String type;

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		String oldrefId = this.refId;
		this.refId = refId;
		support.firePropertyChange("refId", oldrefId, refId);
		entry.set((Integer) FIELD_LIST.get("refId")[0], refId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldname = this.name;
		this.name = name;
		support.firePropertyChange("name", oldname, name);
		entry.set((Integer) FIELD_LIST.get("name")[0], name);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		String oldaddress = this.address;
		this.address = address;
		support.firePropertyChange("address", oldaddress, address);
		entry.set((Integer) FIELD_LIST.get("address")[0], address);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		String oldemail = this.email;
		this.email = email;
		support.firePropertyChange("email", oldemail, email);
		entry.set((Integer) FIELD_LIST.get("email")[0], email);
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		String oldprincipal = this.principal;
		this.principal = principal;
		support.firePropertyChange("principal", oldprincipal, principal);
		entry.set((Integer) FIELD_LIST.get("principal")[0], principal);
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		String oldphoneNo = this.phoneNo;
		this.phoneNo = phoneNo;
		support.firePropertyChange("phoneNo", oldphoneNo, phoneNo);
		entry.set((Integer) FIELD_LIST.get("phoneNo")[0], phoneNo);
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		String olddomainName = this.domainName;
		this.domainName = domainName;
		support.firePropertyChange("domainName", olddomainName, domainName);
		entry.set((Integer) FIELD_LIST.get("domainName")[0], domainName);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "School [refId=" + refId + ", name=" + name + ", address="
				+ address + ", email=" + email + ", principal=" + principal
				+ ", phoneNo=" + phoneNo + ", domainName=" + domainName
				+ ", folderId=" + folderId + ", mobile=" + mobile
				+ ", siteUrl=" + siteUrl + ", metadata=" + metadata
				+ ", staffCount=" + staffCount + ", studentCount="
				+ studentCount + ", type=" + type + "]";
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

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public Integer getStaffCount() {
		return staffCount;
	}

	public void setStaffCount(Integer staffCount) {
		this.staffCount = staffCount;
	}

	public Integer getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Integer studentCount) {
		this.studentCount = studentCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
