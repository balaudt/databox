package com.sagarius.goddess.server.model.neuf;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.sagarius.radix.server.model.BaseEntity;
import org.sagarius.radix.server.model.Metadata;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ShortMessage extends BaseEntity {

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key refId;
	@Persistent
	private String group;
	@Persistent
	private Metadata meta;
	@Persistent
	private String name;
	@Persistent
	private String id;
	@Persistent
	private String phone;
	@Persistent
	private String message;
	@Persistent
	@Extension(vendorName = "datanucleus", key = "gae.parent-pk", value = "true")
	private Key parent;

	@Override
	public String toString() {
		return "ShortMessage [refId=" + refId + ", group=" + group + ", meta="
				+ meta + ", name=" + name + ", id=" + id + ", phone=" + phone
				+ ", message=" + message + ", parent=" + getParent() + "]";
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

	public Key getRefId() {
		return refId;
	}

	public void setRefId(Key refId) {
		this.refId = refId;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Metadata getMeta() {
		return meta;
	}

	public void setMeta(Metadata meta) {
		this.meta = meta;
	}

	public ShortMessage() {
		super(com.sagarius.goddess.client.model.neuf.ShortMessage.FIELD_LIST);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String render() {
		return render(ShortMessage.class);
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setParent(Key parent) {
		this.parent = parent;
	}

	public Key getParent() {
		return parent;
	}

}
